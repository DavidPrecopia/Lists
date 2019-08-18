package com.example.david.lists.data.remote

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.remote.RemoteRepositoryConstants.FIELD_ITEM_USER_LIST_ID
import com.example.david.lists.data.remote.RemoteRepositoryConstants.FIELD_POSITION
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.Type.REMOVED
import com.google.firebase.firestore.EventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import java.util.*

class SnapshotListener(private val userListCollection: CollectionReference,
                       private val itemCollection: CollectionReference,
                       userRepo: IRepositoryContract.UserRepository,
                       firestore: FirebaseFirestore) : IRemoteRepositoryContract.SnapshotListener {

    override val userListFlowable: Flowable<List<UserList>>

    private var userListsSnapshotListener: ListenerRegistration? = null
    private var itemsSnapshotListener: ListenerRegistration? = null

    override val deletedUserListsFlowable: Flowable<List<UserList>>
    private var deletedUserListsEmitter: FlowableEmitter<List<UserList>>? = null

    /**
     * Because there were recent local changes, I can assume that this payload is from the server
     * - which is identical of the query that just came from the local cache - thus it can be skipped.
     */
    private var recentLocalChanges = false

    init {
        deletedUserListsFlowable = initDeletedUserListsFlowable()
        userListFlowable = initUserListFlowable()

        initFirebaseAuth(userRepo, firestore)
    }

    private fun initDeletedUserListsFlowable() =
            Flowable.create<List<UserList>>(
                    { deletedUserListsEmitter = it },
                    BackpressureStrategy.BUFFER
            )

    private fun initUserListFlowable() =
            Flowable.create<List<UserList>>(
                    { getUserListQuerySnapshot(it) },
                    BackpressureStrategy.BUFFER
            )

    private fun initFirebaseAuth(userRepo: IRepositoryContract.UserRepository, firestore: FirebaseFirestore) {
        userRepo.userSignedOutObservable().observeForever { signedOut ->
            if (signedOut) {
                userListsSnapshotListener?.remove()
                itemsSnapshotListener?.remove()
                firestore.clearPersistence()
            }
        }
    }


    private fun getUserListQuerySnapshot(emitter: FlowableEmitter<List<UserList>>) {
        this.userListsSnapshotListener = userListCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getUserListEventListener(emitter))

        emitter.setCancellable {
            userListsSnapshotListener?.remove()
        }
    }

    private fun getUserListEventListener(emitter: FlowableEmitter<List<UserList>>) =
            EventListener<QuerySnapshot> { querySnapshot, e ->
                if (validQuery(querySnapshot, e, emitter)) {
                    emitter.onNext(querySnapshot!!.toObjects(UserList::class.java))
                }

                if (deletedUserListsHasSubscribers()) {
                    checkIfUserListDeleted(querySnapshot!!)
                }
            }

    private fun deletedUserListsHasSubscribers() =
            deletedUserListsEmitter?.isCancelled?.not() ?: false

    private fun checkIfUserListDeleted(querySnapshot: QuerySnapshot) {
        val deletedUserLists = ArrayList<UserList>()
        for (change in querySnapshot.documentChanges) {
            if (change.type == REMOVED) {
                deletedUserLists.add(change.document.toObject(UserList::class.java))
            }
        }

        if (deletedUserLists.isEmpty()) {
            return
        }

        deletedUserListsEmitter!!.onNext(deletedUserLists)
    }


    override fun getItemFlowable(userListId: String) =
            Flowable.create<List<Item>>(
                    { getItemQuerySnapshot(it, userListId) },
                    BackpressureStrategy.BUFFER
            )

    private fun getItemQuerySnapshot(emitter: FlowableEmitter<List<Item>>, userListId: String) {
        itemsSnapshotListener = itemCollection
                .whereEqualTo(FIELD_ITEM_USER_LIST_ID, userListId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getItemEventListener(emitter))

        emitter.setCancellable {
            itemsSnapshotListener?.remove()
        }
    }

    private fun getItemEventListener(emitter: FlowableEmitter<List<Item>>) =
            EventListener<QuerySnapshot> { querySnapshot, e ->
                if (validQuery(querySnapshot, e, emitter)) {
                    emitter.onNext(querySnapshot!!.toObjects(Item::class.java))
                }
            }


    private fun validQuery(querySnapshot: QuerySnapshot?,
                           e: FirebaseFirestoreException?,
                           emitter: FlowableEmitter<*>): Boolean {
        if (querySnapshot === null) {
            return false
        }

        if (queryError(querySnapshot, e)) {
            UtilExceptions.throwException(e!!)
            emitter.onError(e)
            return false
        } else if (shouldReturn(emitter, querySnapshot)) {
            return false
        }
        return true
    }

    private fun queryError(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?): Boolean {
        if (e !== null) {
            return true
        } else if (querySnapshot === null) {
            Crashlytics.log(
                    Log.ERROR,
                    RemoteRepository::class.java.simpleName,
                    "QueryDocumentSnapshot is null"
            )
            return true
        }
        return false
    }

    private fun shouldReturn(emitter: FlowableEmitter<*>, querySnapshot: QuerySnapshot): Boolean {
        if (emitter.isCancelled) {
            return true
        }

        if (recentLocalChanges) {
            recentLocalChanges = recentLocalChanges.not()
            return true
        } else if (fromLocalCache(querySnapshot)) {
            recentLocalChanges = true
            return false
        }
        return false
    }

    /**
     * This payload is from the local cache, post a local change.
     */
    private fun fromLocalCache(querySnapshot: QuerySnapshot) =
            querySnapshot.metadata.hasPendingWrites()
}

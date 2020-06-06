package com.precopia.androiddata.remote

import android.annotation.SuppressLint
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.Type.REMOVED
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.precopia.androiddata.common.createFlowable
import com.precopia.androiddata.datamodel.FirebaseItem
import com.precopia.androiddata.datamodel.FirebaseUserList
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableEmitter
import java.util.*

internal class SnapshotListener(private val userListCollection: CollectionReference,
                                private val itemCollection: CollectionReference,
                                userSignedOutObservable: Flowable<Boolean>,
                                firestore: FirebaseFirestore) : IRemoteRepositoryContract.SnapshotListener {

    private var userListsSnapshotListener: ListenerRegistration? = null
    private var itemsSnapshotListener: ListenerRegistration? = null

    override val deletedUserListsFlowable: Flowable<List<FirebaseUserList>>
    private var deletedUserListsEmitter: FlowableEmitter<List<FirebaseUserList>>? = null

    /**
     * Because there were recent local changes, I can assume that this payload is from the server
     * - which is identical of the query that just came from the local cache - thus it can be skipped.
     */
    private var recentLocalChanges = false


    init {
        deletedUserListsFlowable = initDeletedUserListsFlowable()
        initFirebaseAuth(userSignedOutObservable, firestore)
    }

    private fun initDeletedUserListsFlowable() =
            createFlowable<List<FirebaseUserList>> { deletedUserListsEmitter = it }

    @SuppressLint("CheckResult")
    private fun initFirebaseAuth(userSignedOutObservable: Flowable<Boolean>, firestore: FirebaseFirestore) {
        userSignedOutObservable.subscribe {
            if (it) {
                userListsSnapshotListener?.remove()
                itemsSnapshotListener?.remove()
                firestore.clearPersistence()
            }
        }
    }


    /**
     * USER LIST
     */
    override fun getUserListFlowable() =
            createFlowable<List<FirebaseUserList>> { getUserListQuerySnapshot(it) }

    private fun getUserListQuerySnapshot(emitter: FlowableEmitter<List<FirebaseUserList>>) {
        this.userListsSnapshotListener = userListCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getUserListEventListener(emitter))

        emitter.setCancellable {
            userListsSnapshotListener?.remove()
        }
    }

    private fun getUserListEventListener(emitter: FlowableEmitter<List<FirebaseUserList>>) =
            EventListener<QuerySnapshot> { querySnapshot, e ->
                if (validQuery(querySnapshot, e, emitter)) {
                    emitter.onNext(querySnapshot!!.toObjects())
                    evalDeletedUserList(querySnapshot)
                }
            }

    private fun evalDeletedUserList(querySnapshot: QuerySnapshot) {
        if (deletedUserListsEmitterIsCancelled()) {
            return
        }

        val deletedUserLists = ArrayList<FirebaseUserList>()
        for (change in querySnapshot.documentChanges) {
            if (change.type == REMOVED) {
                deletedUserLists.add(change.document.toObject())
            }
        }

        when {
            deletedUserLists.isEmpty() -> return
            else -> deletedUserListsEmitter!!.onNext(deletedUserLists)
        }
    }

    private fun deletedUserListsEmitterIsCancelled() =
            deletedUserListsEmitter?.isCancelled ?: true


    /**
     * ITEM
     */
    override fun getItemFlowable(userListId: String) =
            createFlowable<List<FirebaseItem>> { getItemQuerySnapshot(it, userListId) }

    private fun getItemQuerySnapshot(emitter: FlowableEmitter<List<FirebaseItem>>, userListId: String) {
        itemsSnapshotListener = itemCollection
                .whereEqualTo(FIELD_ITEM_USER_LIST_ID, userListId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getItemEventListener(emitter))

        emitter.setCancellable {
            itemsSnapshotListener?.remove()
        }
    }

    private fun getItemEventListener(emitter: FlowableEmitter<List<FirebaseItem>>) =
            EventListener<QuerySnapshot> { querySnapshot, e ->
                if (validQuery(querySnapshot, e, emitter)) {
                    emitter.onNext(querySnapshot!!.toObjects())
                }
            }


    /**
     * HELPERS
     */
    private fun validQuery(querySnapshot: QuerySnapshot?,
                           e: FirebaseFirestoreException?,
                           emitter: FlowableEmitter<*>) = when {
        querySnapshot === null -> false
        queryError(querySnapshot, e) -> {
            emitter.onError(e!!)
            false
        }
        shouldReturn(emitter, querySnapshot) -> false
        else -> true
    }

    private fun queryError(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) = when {
        e !== null || querySnapshot === null -> true
        else -> false
    }

    private fun shouldReturn(emitter: FlowableEmitter<*>, querySnapshot: QuerySnapshot) = when {
        emitter.isCancelled -> true
        recentLocalChanges -> {
            recentLocalChanges = recentLocalChanges.not()
            true
        }
        fromLocalCache(querySnapshot) -> {
            recentLocalChanges = true
            false
        }
        else -> false
    }

    /**
     * This payload is from the local cache, post a local change.
     */
    private fun fromLocalCache(querySnapshot: QuerySnapshot) =
            querySnapshot.metadata.hasPendingWrites()
}

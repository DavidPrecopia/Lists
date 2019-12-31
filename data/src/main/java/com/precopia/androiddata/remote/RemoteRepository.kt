package com.precopia.androiddata.remote

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.precopia.androiddata.common.createCompletable
import com.precopia.androiddata.datamodel.FirebaseItem
import com.precopia.androiddata.datamodel.FirebaseUserList
import com.precopia.domain.datamodel.Item
import com.precopia.domain.datamodel.UserList
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Flowable

internal class RemoteRepository(private val firestore: FirebaseFirestore,
                                private val userListsCollection: CollectionReference,
                                private val itemsCollection: CollectionReference,
                                private val snapshotListener: IRemoteRepositoryContract.SnapshotListener) :
        IRemoteRepositoryContract.Repository {


    /**
     * OBSERVE
     */
    override fun getUserLists(): Flowable<List<FirebaseUserList>> =
            snapshotListener.getUserListFlowable()

    override fun getItems(userListId: String) =
            snapshotListener.getItemFlowable(userListId)

    override val userListDeletedObservable: Flowable<List<FirebaseUserList>>
        get() = snapshotListener.deletedUserListsFlowable


    /**
     * ADD
     */
    override fun addUserList(newTitle: String, position: Int): Completable = createCompletable {
        val documentRef = userListsCollection.document()
        val newUserList = FirebaseUserList(newTitle, position, documentRef.id)
        add(documentRef, newUserList, it)
    }

    override fun addItem(newTitle: String, position: Int, userListId: String): Completable = createCompletable {
        val documentRef = itemsCollection.document()
        val newItem = FirebaseItem(newTitle, position, userListId, documentRef.id)
        add(documentRef, newItem, it)
    }

    private fun add(docRef: DocumentReference, any: Any, emitter: CompletableEmitter) {
        docRef.set(any)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
    }


    /**
     * DELETE
     */
    override fun deleteUserLists(userListList: List<UserList>) =
            createCompletable { emitter ->
                firestore.runBatch {
                    for (userList in userListList) {
                        it.delete(getUserListDocument(userList.id))
                    }
                }.addOnSuccessListener { successfullyDeleteUserLists(emitter) }
                        .addOnFailureListener { emitter.onError(it) }
            }

    private fun successfullyDeleteUserLists(emitter: CompletableEmitter) =
            OnSuccessListener<Void> {
                userListsCollection
                        .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener { reorderConsecutively(it, emitter) }
                        .addOnFailureListener { emitter.onError(it) }
            }

    override fun deleteItems(itemList: List<Item>) =
            createCompletable { emitter ->
                firestore.runBatch {
                    for (item in itemList) {
                        it.delete(getItemDocument(item.id))
                    }
                }.addOnSuccessListener(successfullyDeleteItems(itemList[0].userListId, emitter))
                        .addOnFailureListener { emitter.onError(it) }
            }

    private fun successfullyDeleteItems(groupId: String, emitter: CompletableEmitter) =
            OnSuccessListener<Void> {
                itemsCollection
                        .whereEqualTo(FIELD_ITEM_USER_LIST_ID, groupId)
                        .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener { reorderConsecutively(it, emitter) }
                        .addOnFailureListener { emitter.onError(it) }
            }


    /**
     * RENAME
     */
    override fun renameUserList(id: String, newName: String) = createCompletable {
        rename(getUserListDocument(id), newName, it)
    }

    override fun renameItem(id: String, newName: String) = createCompletable {
        rename(getItemDocument(id), newName, it)
    }

    private fun rename(docRef: DocumentReference, newName: String, emitter: CompletableEmitter) {
        docRef.update(FIELD_TITLE, newName)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
    }


    /**
     * POSITION
     */
    override fun updateUserListPosition(id: String, oldPos: Int, newPos: Int) =
            createCompletable {
                updatePosition(
                        getUserListDocument(id),
                        oldPos,
                        newPos,
                        userListSuccessfullyUpdatedListener(it),
                        it
                )
            }

    private fun userListSuccessfullyUpdatedListener(emitter: CompletableEmitter) =
            OnSuccessListener<Void> {
                userListsCollection
                        .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener { reorderConsecutively(it, emitter) }
                        .addOnFailureListener { emitter.onError(it) }
            }


    override fun updateItemPosition(id: String, userListId: String, oldPos: Int, newPos: Int) =
            createCompletable {
                updatePosition(
                        getItemDocument(id),
                        oldPos,
                        newPos,
                        itemSuccessfullyUpdatedListener(userListId, it),
                        it
                )
            }

    private fun itemSuccessfullyUpdatedListener(userListId: String, emitter: CompletableEmitter) =
            OnSuccessListener<Void> {
                itemsCollection
                        .whereEqualTo(FIELD_ITEM_USER_LIST_ID, userListId)
                        .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener { reorderConsecutively(it, emitter) }
                        .addOnFailureListener { emitter.onError(it) }
            }

    private fun updatePosition(docRef: DocumentReference,
                               oldPos: Int,
                               newPos: Int,
                               successListener: OnSuccessListener<in Void>,
                               emitter: CompletableEmitter) {
        when {
            positionsAreTheSame(oldPos, newPos) -> emitter.onComplete()
            positionsAreInvalid(oldPos, newPos) -> emitter.onError(
                    IllegalArgumentException("Positions cannot be less then 0")
            )
            else -> docRef.update(FIELD_POSITION, getNewTempPosition(oldPos, newPos))
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

    private fun positionsAreTheSame(oldPos: Int, newPos: Int) = oldPos == newPos

    private fun positionsAreInvalid(oldPos: Int, newPos: Int) = oldPos < 0 || newPos < 0

    private fun getNewTempPosition(oldPos: Int, newPos: Int) = when {
        newPos > oldPos -> newPos + 0.5
        else -> newPos - 0.5
    }


    /**
     * HELPER
     */
    private fun reorderConsecutively(querySnapshot: QuerySnapshot, emitter: CompletableEmitter) {
        firestore.runBatch {
            for ((index, snapshot) in querySnapshot.documents.withIndex()) {
                if (snapshot.getDouble(FIELD_POSITION) != index.toDouble()) {
                    it.update(snapshot.reference, FIELD_POSITION, index)
                }
            }
        }.addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
    }


    private fun getUserListDocument(id: String) = userListsCollection.document(id)

    private fun getItemDocument(id: String) = itemsCollection.document(id)
}
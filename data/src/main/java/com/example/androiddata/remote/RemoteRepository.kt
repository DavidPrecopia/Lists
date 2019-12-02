package com.example.androiddata.remote

import com.example.androiddata.common.UtilExceptions
import com.example.domain.constants.RemoteRepositoryConstants.FIELD_ITEM_USER_LIST_ID
import com.example.domain.constants.RemoteRepositoryConstants.FIELD_POSITION
import com.example.domain.constants.RemoteRepositoryConstants.FIELD_TITLE
import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import io.reactivex.Flowable

class RemoteRepository(private val firestore: FirebaseFirestore,
                       private val userListsCollection: CollectionReference,
                       private val itemsCollection: CollectionReference,
                       private val snapshotListener: IRemoteRepositoryContract.SnapshotListener) :
        IRemoteRepositoryContract.Repository {


    override val userLists: Flowable<List<UserList>>
        get() = snapshotListener.userListFlowable

    override val userListDeletedObservable: Flowable<List<UserList>>
        get() = snapshotListener.deletedUserListsFlowable

    override fun getItems(userListId: String) =
            snapshotListener.getItemFlowable(userListId)


    override fun addUserList(userList: UserList) {
        val documentRef = userListsCollection.document()
        val newUserList = UserList(userList.title, userList.position, documentRef.id)
        add(documentRef, newUserList)
    }

    override fun addItem(item: Item) {
        val documentRef = itemsCollection.document()
        val newItem = Item(item.title, item.position, item.userListId, documentRef.id)
        add(documentRef, newItem)
    }

    private fun add(docRef: DocumentReference, any: Any) {
        docRef.set(any)
                .addOnFailureListener { onFailure(it) }
    }


    override fun deleteUserLists(userListList: List<UserList>) {
        firestore.runBatch {
            for (userList in userListList) {
                it.delete(getUserListDocument(userList.id))
            }
        }.addOnSuccessListener { successfullyDeleteUserLists() }
                .addOnFailureListener { onFailure(it) }
    }

    private fun successfullyDeleteUserLists() = OnSuccessListener<Void> {
        userListsCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { reorderConsecutively(it) }
                .addOnFailureListener { onFailure(it) }
    }

    override fun deleteItems(itemList: List<Item>) {
        firestore.runBatch {
            for (item in itemList) {
                it.delete(getItemDocument(item.id))
            }
        }.addOnSuccessListener(successfullyDeleteItems(itemList[0].userListId))
                .addOnFailureListener { onFailure(it) }
    }

    private fun successfullyDeleteItems(groupId: String) = OnSuccessListener<Void> {
        itemsCollection
                .whereEqualTo(FIELD_ITEM_USER_LIST_ID, groupId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { reorderConsecutively(it) }
                .addOnFailureListener { onFailure(it) }
    }


    override fun renameUserList(userListId: String, newName: String) {
        rename(getUserListDocument(userListId), newName)
    }

    override fun renameItem(itemId: String, newName: String) {
        rename(getItemDocument(itemId), newName)
    }

    private fun rename(docRef: DocumentReference, newName: String) {
        docRef.update(FIELD_TITLE, newName)
                .addOnFailureListener { onFailure(it) }
    }


    override fun updateUserListPosition(userList: UserList, oldPosition: Int, newPosition: Int) {
        update(getUserListDocument(userList.id), oldPosition, newPosition, userListSuccessfullyUpdatedListener())
    }

    private fun userListSuccessfullyUpdatedListener() = OnSuccessListener<Void> {
        userListsCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { reorderConsecutively(it) }
                .addOnFailureListener { onFailure(it) }
    }


    override fun updateItemPosition(item: Item, oldPosition: Int, newPosition: Int) {
        update(getItemDocument(item.id), oldPosition, newPosition, itemSuccessfullyUpdatedListener(item.userListId))
    }

    private fun itemSuccessfullyUpdatedListener(userListId: String) = OnSuccessListener<Void> {
        itemsCollection
                .whereEqualTo(FIELD_ITEM_USER_LIST_ID, userListId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { reorderConsecutively(it) }
                .addOnFailureListener { onFailure(it) }
    }


    private fun update(docRef: DocumentReference, oldPosition: Int, newPosition: Int, successListener: OnSuccessListener<in Void>) {
        docRef.update(FIELD_POSITION, getNewTempPosition(oldPosition, newPosition))
                .addOnSuccessListener(successListener)
                .addOnFailureListener { onFailure(it) }

    }

    private fun getNewTempPosition(oldPosition: Int, newPosition: Int) = when {
        newPosition > oldPosition -> newPosition + 0.5
        else -> newPosition - 0.5
    }


    private fun reorderConsecutively(querySnapshot: QuerySnapshot) {
        firestore.runBatch {
            for ((index, snapshot) in querySnapshot.documents.withIndex()) {
                if (snapshot.getDouble(FIELD_POSITION) != index.toDouble()) {
                    it.update(snapshot.reference, FIELD_POSITION, index)
                }
            }
        }.addOnFailureListener { onFailure(it) }
    }


    private fun getUserListDocument(groupId: String) = userListsCollection.document(groupId)

    private fun getItemDocument(itemId: String) = itemsCollection.document(itemId)


    private fun onFailure(exception: Exception) {
        UtilExceptions.throwException(exception)
    }
}
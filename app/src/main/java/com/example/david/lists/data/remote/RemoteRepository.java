package com.example.david.lists.data.remote;

import androidx.lifecycle.LiveData;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.util.UtilExceptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import io.reactivex.Flowable;

import static com.example.david.lists.data.remote.RemoteRepositoryConstants.FIELD_ITEM_LIST_ID;
import static com.example.david.lists.data.remote.RemoteRepositoryConstants.FIELD_POSITION;
import static com.example.david.lists.data.remote.RemoteRepositoryConstants.FIELD_TITLE;

public final class RemoteRepository implements IRemoteRepositoryContract.Repository {

    private final FirebaseFirestore firestore;
    private final CollectionReference userListsCollection;
    private final CollectionReference itemsCollection;

    private final IRemoteRepositoryContract.SnapshotListener snapshotListener;

    public RemoteRepository(FirebaseFirestore firestore,
                            CollectionReference userListsCollection,
                            CollectionReference itemsCollection,
                            IRemoteRepositoryContract.SnapshotListener snapshotListener) {
        this.firestore = firestore;
        this.userListsCollection = userListsCollection;
        this.itemsCollection = itemsCollection;
        this.snapshotListener = snapshotListener;
    }


    @Override
    public Flowable<List<UserList>> getUserLists() {
        return snapshotListener.getUserListFlowable();
    }

    @Override
    public Flowable<List<Item>> getItems(String userListId) {
        return snapshotListener.getItemFlowable(userListId);
    }


    @Override
    public void addUserList(UserList userList) {
        DocumentReference documentRef = userListsCollection.document();
        UserList newUserList = new UserList(documentRef.getId(), userList);
        add(documentRef, newUserList);
    }

    @Override
    public void addItem(Item item) {
        DocumentReference documentRef = itemsCollection.document();
        Item newItem = new Item(documentRef.getId(), item);
        add(documentRef, newItem);
    }

    private void add(DocumentReference documentRef, Object object) {
        documentRef.set(object)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void deleteUserLists(List<UserList> userListList) {
        firestore.runBatch(writeBatch -> {
            for (UserList userList : userListList) {
                writeBatch.delete(getUserListDocument(userList.getId()));
            }
        }).addOnSuccessListener(successfullyDeleteUserLists())
                .addOnFailureListener(this::onFailure);
    }

    private OnSuccessListener<Void> successfullyDeleteUserLists() {
        return aVoid -> userListsCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(this::reorderConsecutively)
                .addOnFailureListener(this::onFailure);
    }

    @Override
    public void deleteItems(List<Item> itemList) {
        firestore.runBatch(writeBatch -> {
            for (Item item : itemList) {
                writeBatch.delete(getItemDocument(item.getId()));
            }
        }).addOnSuccessListener(successfullyDeleteItems(itemList.get(0).getUserListId()))
                .addOnFailureListener(this::onFailure);
    }

    private OnSuccessListener<Void> successfullyDeleteItems(String groupId) {
        return aVoid -> itemsCollection
                .whereEqualTo(FIELD_ITEM_LIST_ID, groupId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(this::reorderConsecutively)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void renameUserList(String userListId, String newName) {
        rename(getUserListDocument(userListId), newName);
    }

    @Override
    public void renameItem(String itemId, String newName) {
        rename(getItemDocument(itemId), newName);
    }

    private void rename(DocumentReference documentReference, String newName) {
        documentReference
                .update(FIELD_TITLE, newName)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void updateUserListPosition(UserList userList, int oldPosition, int newPosition) {
        getUserListDocument(userList.getId())
                .update(FIELD_POSITION, getNewTemporaryPosition(oldPosition, newPosition))
                .addOnSuccessListener(aVoid ->
                        userListsCollection
                                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener(this::reorderConsecutively)
                                .addOnFailureListener(this::onFailure)
                )
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void updateItemPosition(Item item, int oldPosition, int newPosition) {
        getItemDocument(item.getId())
                .update(FIELD_POSITION, getNewTemporaryPosition(oldPosition, newPosition))
                .addOnSuccessListener(aVoid ->
                        itemsCollection
                                .whereEqualTo(FIELD_ITEM_LIST_ID, item.getUserListId())
                                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener(this::reorderConsecutively)
                                .addOnFailureListener(this::onFailure)
                )
                .addOnFailureListener(this::onFailure);
    }

    private double getNewTemporaryPosition(int oldPosition, int newPosition) {
        return newPosition > oldPosition ? newPosition + 0.5 : newPosition - 0.5;
    }


    private void reorderConsecutively(QuerySnapshot queryDocumentSnapshots) {
        firestore.runBatch(writeBatch -> {
            int newPosition = 0;
            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                if (snapshot.getDouble(FIELD_POSITION) != newPosition) {
                    writeBatch.update(snapshot.getReference(), FIELD_POSITION, newPosition);
                }
                newPosition++;
            }
        }).addOnFailureListener(this::onFailure);
    }


    private DocumentReference getUserListDocument(String groupId) {
        return userListsCollection.document(groupId);
    }

    private DocumentReference getItemDocument(String itemId) {
        return itemsCollection.document(itemId);
    }


    @Override
    public LiveData<List<UserList>> getEventUserListDeleted() {
        return snapshotListener.getEventDeleteUserList();
    }


    private void onFailure(Exception exception) {
        UtilExceptions.throwException(exception);
    }
}
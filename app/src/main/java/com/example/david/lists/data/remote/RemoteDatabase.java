package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.example.david.lists.data.remote.RemoteDatabaseConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_LISTS_COLLECTION;

public final class RemoteDatabase implements IRemoteDatabaseContract {

    private final FirebaseFirestore firestore;
    private final CollectionReference userListsCollection;
    private final CollectionReference itemsCollection;

    private static RemoteDatabase instance;

    public static RemoteDatabase getInstance() {
        if (instance == null) {
            instance = new RemoteDatabase();
        }
        return instance;
    }

    private RemoteDatabase() {
        firestore = FirebaseFirestore.getInstance();
        userListsCollection = firestore.collection(USER_LISTS_COLLECTION);
        itemsCollection = firestore.collection(ITEMS_COLLECTION);
    }


    @Override
    public void addUserList(UserList userList) {
        getUserListDocument(userList.getId())
                .set(userList)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void addItem(Item item) {
        getItemDocument(item.getId())
                .set(item)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        List<Integer> userListIds = batchDeleteUserLists(userLists);
        prepareToBatchDeleteItems(userListIds);
    }

    private List<Integer> batchDeleteUserLists(List<UserList> userLists) {
        List<Integer> userListIds = new ArrayList<>();

        WriteBatch writeBatch = firestore.batch();
        for (UserList userList : userLists) {
            int id = userList.getId();
            userListIds.add(id);
            writeBatch.delete(getUserListDocument(id));
        }
        writeBatch.commit().addOnFailureListener(this::onFailure);

        return userListIds;
    }

    private void prepareToBatchDeleteItems(List<Integer> userListIds) {
        for (Integer userListId : userListIds) {
            itemsCollection
                    .whereEqualTo("userListId", userListId)
                    .get()
                    .addOnSuccessListener(this::batchDeleteItems)
                    .addOnFailureListener(this::onFailure);
        }
    }

    private void batchDeleteItems(QuerySnapshot queryDocumentSnapshots) {
        WriteBatch batch = firestore.batch();
        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
            batch.delete(snapshot.getReference());
        }
        batch.commit().addOnFailureListener(this::onFailure);
    }


    @Override
    public void deleteItems(List<Item> items) {
        WriteBatch batch = firestore.batch();
        for (Item item : items) {
            batch.delete(getItemDocument(item.getId()));
        }
        batch.commit().addOnFailureListener(this::onFailure);
    }


    @Override
    public void renameUserList(int userListId, String newName) {
        getUserListDocument(userListId)
                .update("title", newName)
                .addOnFailureListener(this::onFailure);
    }

    @Override
    public void renameItem(int itemId, String newName) {
        getItemDocument(itemId)
                .update("title", newName)
                .addOnFailureListener(this::onFailure);
    }

    private DocumentReference getUserListDocument(int userListId) {
        return userListsCollection.document(intToString(userListId));
    }

    private DocumentReference getItemDocument(int itemId) {
        return itemsCollection.document(intToString(itemId));
    }


    private void onFailure(Exception exception) {
        Timber.e(exception);
    }

    private String intToString(int id) {
        return String.valueOf(id);
    }
}

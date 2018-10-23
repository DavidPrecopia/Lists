package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;

import static com.example.david.lists.data.remote.RemoteDatabaseConstants.TESTING_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.TESTING_SUB_COLLECTION;

public final class RemoteDatabase implements IRemoteDatabaseContract {

    private final FirebaseFirestore firestore;

    private static RemoteDatabase instance;

    public static RemoteDatabase getInstance() {
        if (instance == null) {
            instance = new RemoteDatabase();
        }
        return instance;
    }

    private RemoteDatabase() {
        firestore = FirebaseFirestore.getInstance();
    }


    @Override
    public void addUserList(UserList userList) {
        firestore.collection(TESTING_COLLECTION)
                .document(intToString(userList.getId()))
                .set(userList)
                .addOnFailureListener(Timber::e);
    }

    @Override
    public void addItem(Item item) {
        firestore.collection(TESTING_COLLECTION)
                .document(intToString(item.getListId()))
                .collection(TESTING_SUB_COLLECTION)
                .document(intToString(item.getId()))
                .set(item)
                .addOnFailureListener(Timber::e);
    }


    private String intToString(int id) {
        return String.valueOf(id);
    }
}

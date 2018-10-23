package com.example.david.lists.data.remote;

import android.app.Application;

import com.example.david.lists.data.datamodel.UserList;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import timber.log.Timber;

import static com.example.david.lists.data.remote.RemoteDatabaseConstants.TESTING_COLLECTION_NAME;

public final class RemoteDatabase implements IRemoteDatabaseContract {

    private final FirebaseFirestore firestore;

    private static RemoteDatabase instance;

    public static RemoteDatabase getInstance(Application application) {
        if (instance == null) {
            instance = new RemoteDatabase(application);
        }
        return instance;
    }

    private RemoteDatabase(Application application) {
        firestore = FirebaseFirestore.getInstance();
    }


    @Override
    public void addUserList(UserList userList) {
        firestore.collection(TESTING_COLLECTION_NAME)
                .document(userList.getTitle())
                .set(userList)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.i("UserList set was successful");
                    } else {
                        Timber.e(task.getException());
                    }
                });
    }


    private boolean haveException(FirebaseFirestoreException exception) {
        return exception != null;
    }

    private boolean validSnapshot(DocumentSnapshot documentSnapshot) {
        return documentSnapshot != null && documentSnapshot.exists();
    }
}

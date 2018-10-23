package com.example.david.lists.data.remote;

import android.app.Application;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.local.LocalDao;
import com.example.david.lists.data.local.LocalDatabase;
import com.example.david.lists.util.UtilRxJava;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import io.reactivex.Completable;
import timber.log.Timber;

public final class RemoteDatabase implements IRemoteDatabaseContract {

    private final FirebaseFirestore firestore;
    private final LocalDao localDao;

    private final static String COLLECTION = "user_lists";

    public RemoteDatabase(Application application) {
        firestore = FirebaseFirestore.getInstance();
        localDao = LocalDatabase.getInstance(application).getLocalDao();
    }

    public void practice(UserList userList) {
        addToRemote(userList);
        observeDocument(userList);
    }

    private void addToRemote(UserList userList) {
        firestore.collection(COLLECTION)
                .document(userList.getTitle())
                .set(userList)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Timber.e(task.getException());
                    }
                    Timber.i("UserList set was successful");
                });
    }

    private void observeDocument(UserList userList) {
        firestore.collection(COLLECTION).document(userList.getTitle())
                .addSnapshotListener(((documentSnapshot, e) -> {
                    if (haveException(e)) {
                        Timber.e(e);
                    }

                    if (validSnapshot(documentSnapshot)) {
                        // TESTING ONLY
                        // directly accessing the Dao
                        // instead of going through the Model
                        UtilRxJava.completableIoAccess(
                                Completable.fromAction(() ->
                                        localDao.addUserList(documentSnapshot.toObject(UserList.class))
                                )
                        );
                    }
                }));
    }

    private boolean haveException(FirebaseFirestoreException e) {
        return e != null;
    }

    private boolean validSnapshot(DocumentSnapshot documentSnapshot) {
        return documentSnapshot != null && documentSnapshot.exists();
    }
}

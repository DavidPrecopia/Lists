package com.example.david.lists.di.data;

import com.example.david.lists.data.remote.IRemoteStorageContract;
import com.example.david.lists.data.remote.RemoteStorage;
import com.example.david.lists.data.remote.UtilSnapshotListeners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.example.david.lists.data.remote.RemoteStorageConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteStorageConstants.USER_COLLECTION;
import static com.example.david.lists.data.remote.RemoteStorageConstants.USER_LISTS_COLLECTION;

@Module
class RemoteStorageModule {
    @Singleton
    @Provides
    IRemoteStorageContract remoteStorage(FirebaseFirestore firestore,
                                         @Named(USER_LISTS_COLLECTION) CollectionReference userListCollection,
                                         @Named(ITEMS_COLLECTION) CollectionReference itemCollection,
                                         UtilSnapshotListeners snapshotListeners) {
        return new RemoteStorage(firestore, userListCollection, itemCollection, snapshotListeners);
    }

    @Singleton
    @Provides
    FirebaseFirestore firebaseFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        );
        return firestore;
    }

    @Singleton
    @Provides
    UtilSnapshotListeners utilSnapshotListeners(@Named(USER_LISTS_COLLECTION) CollectionReference userListCollection,
                                                @Named(ITEMS_COLLECTION) CollectionReference itemCollection,
                                                FirebaseAuth auth) {
        return new UtilSnapshotListeners(userListCollection, itemCollection, auth);
    }

    @Named(USER_LISTS_COLLECTION)
    @Singleton
    @Provides
    CollectionReference userListCollectionReference(@Named(USER_COLLECTION) DocumentReference userDocument) {
        return userDocument.collection(USER_LISTS_COLLECTION);
    }

    @Named(ITEMS_COLLECTION)
    @Singleton
    @Provides
    CollectionReference itemCollectionReference(@Named(USER_COLLECTION) DocumentReference userDocument) {
        return userDocument.collection(ITEMS_COLLECTION);
    }

    @Named(USER_COLLECTION)
    @Singleton
    @Provides
    DocumentReference userIdDocument(FirebaseFirestore firestore, FirebaseAuth auth) {
        String uid = auth.getCurrentUser().getUid();
        return firestore.collection(USER_COLLECTION).document(uid);
    }

    @Singleton
    @Provides
    FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}

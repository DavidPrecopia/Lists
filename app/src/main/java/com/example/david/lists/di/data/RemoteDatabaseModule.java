package com.example.david.lists.di.data;

import com.example.david.lists.data.remote.IRemoteDatabaseContract;
import com.example.david.lists.data.remote.RemoteDatabase;
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

import static com.example.david.lists.data.remote.RemoteDatabaseConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_LISTS_COLLECTION;

@Module
class RemoteDatabaseModule {
    @Singleton
    @Provides
    IRemoteDatabaseContract remoteDatabase(FirebaseFirestore firestore,
                                           @Named(USER_LISTS_COLLECTION) CollectionReference userListCollection,
                                           @Named(ITEMS_COLLECTION) CollectionReference itemCollection,
                                           UtilSnapshotListeners snapshotListeners) {
        return new RemoteDatabase(firestore, userListCollection, itemCollection, snapshotListeners);
    }

    @Singleton
    @Provides
    FirebaseFirestore firebaseFirestore(FirebaseFirestoreSettings settings) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(settings);
        return firestore;
    }

    @Singleton
    @Provides
    FirebaseFirestoreSettings firebaseFirestoreSettings() {
        final long cacheSize = 10 * 1024 * 1024; // 10mb
        return new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(cacheSize)
                .build();
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

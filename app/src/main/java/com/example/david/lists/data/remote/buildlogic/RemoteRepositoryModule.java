package com.example.david.lists.data.remote.buildlogic;

import com.example.david.lists.data.remote.IRemoteRepositoryContract;
import com.example.david.lists.data.remote.RemoteRepository;
import com.example.david.lists.data.remote.UtilSnapshotListeners;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.example.david.lists.data.remote.RemoteRepositoryConstants.COLLECTION_ITEMS;
import static com.example.david.lists.data.remote.RemoteRepositoryConstants.COLLECTION_USER;
import static com.example.david.lists.data.remote.RemoteRepositoryConstants.COLLECTION_USER_LISTS;

@Module
public final class RemoteRepositoryModule {
    @Singleton
    @Provides
    IRemoteRepositoryContract.Repository remoteDatabase(FirebaseFirestore firestore,
                                                        @Named(COLLECTION_USER_LISTS) CollectionReference userListCollection,
                                                        @Named(COLLECTION_ITEMS) CollectionReference itemCollection,
                                                        UtilSnapshotListeners snapshotListeners) {
        return new RemoteRepository(firestore, userListCollection, itemCollection, snapshotListeners);
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
    UtilSnapshotListeners utilSnapshotListeners(@Named(COLLECTION_USER_LISTS) CollectionReference userListCollection,
                                                @Named(COLLECTION_ITEMS) CollectionReference itemCollection,
                                                IRepositoryContract.UserRepository userRepository,
                                                FirebaseFirestore firestore) {
        return new UtilSnapshotListeners(userListCollection, itemCollection, userRepository, firestore);
    }

    @Named(COLLECTION_USER_LISTS)
    @Singleton
    @Provides
    CollectionReference userListCollectionReference(@Named(COLLECTION_USER) DocumentReference userDocument) {
        return userDocument.collection(COLLECTION_USER_LISTS);
    }

    @Named(COLLECTION_ITEMS)
    @Singleton
    @Provides
    CollectionReference itemCollectionReference(@Named(COLLECTION_USER) DocumentReference userDocument) {
        return userDocument.collection(COLLECTION_ITEMS);
    }

    @Named(COLLECTION_USER)
    @Singleton
    @Provides
    DocumentReference userIdDocument(FirebaseFirestore firestore, FirebaseAuth auth) {
        String uid = auth.getCurrentUser().getUid();
        return firestore.collection(COLLECTION_USER).document(uid);
    }
}

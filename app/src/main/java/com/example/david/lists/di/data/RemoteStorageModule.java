package com.example.david.lists.di.data;

import com.example.david.lists.data.remote.IRemoteStorageContract;
import com.example.david.lists.data.remote.RemoteStorage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class RemoteStorageModule {
    @Singleton
    @Provides
    IRemoteStorageContract remoteStorage(FirebaseFirestore firestore) {
        return new RemoteStorage(firestore);
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
}

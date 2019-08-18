package com.example.david.lists.data.remote.buildlogic

import com.example.david.lists.data.remote.IRemoteRepositoryContract
import com.example.david.lists.data.remote.RemoteRepository
import com.example.david.lists.data.remote.RemoteRepositoryConstants.COLLECTION_ITEMS
import com.example.david.lists.data.remote.RemoteRepositoryConstants.COLLECTION_USER
import com.example.david.lists.data.remote.RemoteRepositoryConstants.COLLECTION_USER_LISTS
import com.example.david.lists.data.remote.SnapshotListener
import com.example.david.lists.data.repository.IRepositoryContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class RemoteRepositoryModule {
    @Singleton
    @Provides
    fun remoteDatabase(firestore: FirebaseFirestore,
                       @Named(COLLECTION_USER_LISTS) userListCollection: CollectionReference,
                       @Named(COLLECTION_ITEMS) itemCollection: CollectionReference,
                       snapshotListener: IRemoteRepositoryContract.SnapshotListener): IRemoteRepositoryContract.Repository {
        return RemoteRepository(firestore, userListCollection, itemCollection, snapshotListener)
    }

    @Singleton
    @Provides
    fun firebaseFirestore(settings: FirebaseFirestoreSettings): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            firestoreSettings = settings
        }
    }

    @Singleton
    @Provides
    fun firebaseFirestoreSettings(): FirebaseFirestoreSettings {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10mb
        return FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(cacheSize)
                .build()
    }

    @Singleton
    @Provides
    fun snapshotListener(@Named(COLLECTION_USER_LISTS) userListCollection: CollectionReference,
                         @Named(COLLECTION_ITEMS) itemCollection: CollectionReference,
                         userRepo: IRepositoryContract.UserRepository,
                         firestore: FirebaseFirestore): IRemoteRepositoryContract.SnapshotListener {
        return SnapshotListener(userListCollection, itemCollection, userRepo, firestore)
    }

    @Singleton
    @Provides
    @Named(COLLECTION_USER_LISTS)
    fun userListCollectionReference(@Named(COLLECTION_USER) userDocument: DocumentReference): CollectionReference {
        return userDocument.collection(COLLECTION_USER_LISTS)
    }

    @Singleton
    @Provides
    @Named(COLLECTION_ITEMS)
    fun itemCollectionReference(@Named(COLLECTION_USER) userDocument: DocumentReference): CollectionReference {
        return userDocument.collection(COLLECTION_ITEMS)
    }

    @Singleton
    @Provides
    @Named(COLLECTION_USER)
    fun userIdDocument(firestore: FirebaseFirestore, auth: FirebaseAuth): DocumentReference {
        val uid = auth.currentUser!!.uid
        return firestore.collection(COLLECTION_USER).document(uid)
    }
}

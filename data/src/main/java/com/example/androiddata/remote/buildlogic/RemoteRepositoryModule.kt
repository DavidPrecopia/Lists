package com.example.androiddata.remote.buildlogic

import com.example.androiddata.common.DataScope
import com.example.androiddata.remote.*
import com.example.domain.repository.IRepositoryContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
internal class RemoteRepositoryModule {
    @DataScope
    @Provides
    fun remoteDatabase(firestore: FirebaseFirestore,
                       @Named(COLLECTION_USER_LISTS) userListCollection: CollectionReference,
                       @Named(COLLECTION_ITEMS) itemCollection: CollectionReference,
                       snapshotListener: IRemoteRepositoryContract.SnapshotListener): IRemoteRepositoryContract.Repository {
        return RemoteRepository(firestore, userListCollection, itemCollection, snapshotListener)
    }

    @DataScope
    @Provides
    fun firebaseFirestore(settings: FirebaseFirestoreSettings): FirebaseFirestore {
        return Firebase.firestore.apply {
            firestoreSettings = settings
        }
    }

    @DataScope
    @Provides
    fun firebaseFirestoreSettings(): FirebaseFirestoreSettings {
        val cacheSize = (5 * 1024 * 1024).toLong() // 5mb
        return FirebaseFirestoreSettings.Builder()
                // This specifies the cache size threshold - default is 40mb.
                .setCacheSizeBytes(cacheSize)
                .build()
    }

    @DataScope
    @Provides
    fun snapshotListener(@Named(COLLECTION_USER_LISTS) userListCollection: CollectionReference,
                         @Named(COLLECTION_ITEMS) itemCollection: CollectionReference,
                         userRepo: IRepositoryContract.UserRepository,
                         firestore: FirebaseFirestore): IRemoteRepositoryContract.SnapshotListener {
        return SnapshotListener(userListCollection, itemCollection, userRepo, firestore)
    }

    @DataScope
    @Provides
    @Named(COLLECTION_USER_LISTS)
    fun userListCollectionReference(@Named(COLLECTION_USER) userDocument: DocumentReference): CollectionReference {
        return userDocument.collection(COLLECTION_USER_LISTS)
    }

    @DataScope
    @Provides
    @Named(COLLECTION_ITEMS)
    fun itemCollectionReference(@Named(COLLECTION_USER) userDocument: DocumentReference): CollectionReference {
        return userDocument.collection(COLLECTION_ITEMS)
    }

    @DataScope
    @Provides
    @Named(COLLECTION_USER)
    fun userIdDocument(firestore: FirebaseFirestore, auth: FirebaseAuth): DocumentReference {
        val uid = auth.currentUser!!.uid
        return firestore.collection(COLLECTION_USER).document(uid)
    }
}

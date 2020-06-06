package com.precopia.androiddata.remote.buildlogic

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.precopia.androiddata.remote.*
import io.reactivex.rxjava3.core.Flowable

internal class RemoteRepoModule(private val userSignedOutObservable: Flowable<Boolean>) {

    fun remoteRepo() = RemoteRepository(
            firestore(),
            userListCollectionRef(),
            itemCollectionRef(),
            snapshotListener()
    )


    private fun firestore() =
            Firebase.firestore.apply {
                firestoreSettings = firebaseFirestoreSettings()
            }

    private fun firebaseFirestoreSettings(): FirebaseFirestoreSettings {
        val cacheSize = (5 * 1024 * 1024).toLong() // 5mb
        return FirebaseFirestoreSettings.Builder()
                // This specifies the cache size threshold - default is 40mb.
                .setCacheSizeBytes(cacheSize)
                .build()
    }

    private fun snapshotListener() = SnapshotListener(
            userListCollectionRef(),
            itemCollectionRef(),
            userSignedOutObservable,
            firestore()
    )


    private fun userListCollectionRef() = userDocument().collection(COLLECTION_USER_LISTS)

    private fun itemCollectionRef() = userDocument().collection(COLLECTION_ITEMS)

    private fun userDocument(): DocumentReference {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        return firestore().collection(COLLECTION_USER).document(uid)
    }
}

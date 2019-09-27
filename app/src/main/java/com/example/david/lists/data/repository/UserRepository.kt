package com.example.david.lists.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class UserRepository(private val firebaseAuth: FirebaseAuth) :
        IRepositoryContract.UserRepository {

    private val userSignedOut = MutableLiveData<Boolean>()

    override val signedOut
        get() = firebaseAuth.currentUser == null

    init {
        firebaseAuth.addAuthStateListener {
            if (this.signedOut) {
                userSignedOut.value = true
            }
        }
    }

    override fun userSignedOutObservable() = userSignedOut
}

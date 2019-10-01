package com.example.david.lists.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * I do not have [FirebaseUser] as a constructor parameter because
 * retrieving the user via [FirebaseAuth.getCurrentUser] ensures that
 * I am getting fresh information.
 */
class UserRepository(private val firebaseAuth: FirebaseAuth) :
        IRepositoryContract.UserRepository {

    private val userSignedOutObservable = MutableLiveData<Boolean>()

    private val userNullException = NullPointerException("User is null")

    init {
        firebaseAuth.addAuthStateListener {
            if (this.signedOut) {
                userSignedOutObservable.value = true
            }
        }
    }


    override val user: FirebaseUser?
        get() = firebaseAuth.currentUser

    override val email: String?
        get() = user?.email


    override val signedOut
        get() = user === null

    override val userVerified
        get() = signedOut.not() && hasEmail.not() || hasEmail && emailVerified


    override val hasEmail
        get() = user?.email?.isNotEmpty() ?: false

    override val emailVerified
        get() = user?.isEmailVerified ?: false


    override fun sendVerificationEmail(successListener: OnSuccessListener<in Void>,
                                       failureListener: OnFailureListener) {
        user?.sendEmailVerification()
                ?.addOnSuccessListener(successListener)
                ?.addOnFailureListener(failureListener)
                ?: failureListener.onFailure(userNullException)
    }

    override fun reloadUser(successListener: OnSuccessListener<in Void>,
                            failureListener: OnFailureListener) {
        user?.reload()
                ?.addOnSuccessListener(successListener)
                ?.addOnFailureListener(failureListener)
                ?: failureListener.onFailure(userNullException)
    }

    override fun userSignedOutObservable() = userSignedOutObservable
}

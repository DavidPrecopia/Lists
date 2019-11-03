package com.example.david.lists.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult

/**
 * I do not have [FirebaseUser] as a constructor parameter because
 * retrieving the user via [FirebaseAuth.getCurrentUser] ensures that
 * I am getting fresh information.
 */
class UserRepository(private val firebaseAuth: FirebaseAuth,
                     private val actionCodeSettings: ActionCodeSettings,
                     private val authUI: AuthUI,
                     private val application: Application) :
        IRepositoryContract.UserRepository {

    private val user: FirebaseUser?
        get() = firebaseAuth.currentUser

    private val userSignedOutObservable = MutableLiveData<Boolean>()

    private val userNullException = NullPointerException("User is null")

    init {
        firebaseAuth.addAuthStateListener {
            if (this.signedOut) {
                userSignedOutObservable.value = true
            }
        }
    }


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
        user?.sendEmailVerification(actionCodeSettings)
                ?.addOnSuccessListener(successListener)
                ?.addOnFailureListener(failureListener)
                ?: failureListener.onFailure(userNullException)
    }

    override fun reloadUser(successListener: OnSuccessListener<in Void>,
                            failureListener: OnFailureListener) {
        user?.getIdToken(true)
                ?.addOnSuccessListener(reload(successListener, failureListener))
                ?.addOnFailureListener(failureListener)
                ?: failureListener.onFailure(userNullException)
    }

    private fun reload(successListener: OnSuccessListener<in Void>,
                       failureListener: OnFailureListener) = OnSuccessListener<GetTokenResult> {
        user?.reload()
                ?.addOnSuccessListener(successListener)
                ?.addOnFailureListener(failureListener)
                ?: failureListener.onFailure(userNullException)
    }


    override fun deleteUser(successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener) {
        authUI.delete(application)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
    }


    override fun userSignedOutObservable() = userSignedOutObservable
}

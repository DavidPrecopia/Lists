package com.example.david.lists.data.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.david.lists.data.repository.IRepositoryContract.Providers
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

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


    /**
     * I am explicitly getting the provider ID at index 1 because
     * the provider ID at index 0 will always be "firebase".
     */
    override val authProvider: Providers
        get() = when (user!!.providerData[1].providerId) {
            GoogleAuthProvider.PROVIDER_ID -> Providers.GOOGLE
            EmailAuthProvider.PROVIDER_ID -> Providers.EMAIL
            PhoneAuthProvider.PROVIDER_ID -> Providers.PHONE
            else -> Providers.UNKNOWN
        }


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


    override fun signOut(successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener) {
        authUI.signOut(application)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
    }


    override fun validatePhoneNumber(phoneNum: String, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                phoneNum,
                SMS_TIME_OUT_SECONDS,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                callbacks
        )
    }


    override fun deleteGoogleUser(successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener) {
        deleteAccount(
                GoogleAuthProvider.getCredential(
                        GoogleSignIn.getLastSignedInAccount(application)!!.idToken,
                        null),
                successListener,
                failureListener
        )
    }

    override fun deleteEmailUser(password: String,
                                 successListener: OnSuccessListener<in Void>,
                                 failureListener: OnFailureListener) {
        deleteAccount(
                EmailAuthProvider.getCredential(email!!, password),
                successListener,
                failureListener
        )
    }

    override fun deletePhoneUser(verificationId: String,
                                 smsCode: String,
                                 successListener: OnSuccessListener<in Void>,
                                 failureListener: OnFailureListener) {
        deleteAccount(
                PhoneAuthProvider.getCredential(verificationId, smsCode),
                successListener,
                failureListener
        )
    }

    private fun deleteAccount(authCredential: AuthCredential,
                              successListener: OnSuccessListener<in Void>,
                              failureListener: OnFailureListener) {
        user!!.reauthenticate(authCredential)
                .addOnSuccessListener {
                    authUI.delete(application)
                            .addOnSuccessListener(successListener)
                            .addOnFailureListener(failureListener)
                }.addOnFailureListener(failureListener)
    }


    override fun userSignedOutObservable() = userSignedOutObservable
}

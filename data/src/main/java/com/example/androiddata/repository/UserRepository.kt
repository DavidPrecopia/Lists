package com.example.androiddata.repository

import android.app.Application
import com.example.androiddata.common.createCompletable
import com.example.domain.constants.AuthProviders
import com.example.domain.constants.PHONE_NUM_COUNTRY_CODE_USA
import com.example.domain.constants.PhoneNumValidationResults
import com.example.domain.constants.SMS_TIME_OUT_SECONDS
import com.example.domain.repository.IRepositoryContract
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import io.reactivex.*
import java.util.concurrent.TimeUnit

/**
 * I do not have [FirebaseUser] as a constructor parameter because
 * retrieving the user via [FirebaseAuth.getCurrentUser] ensures that
 * I am getting fresh information.
 */
internal class UserRepository(private val firebaseAuth: FirebaseAuth,
                     private val actionCodeSettings: ActionCodeSettings,
                     private val authUI: AuthUI,
                     private val application: Application) :
        IRepositoryContract.UserRepository {

    private val user: FirebaseUser?
        get() = firebaseAuth.currentUser

    private val userSignedOutObservable: Flowable<Boolean>

    private val userNullException = NullPointerException("User is null")

    init {
        userSignedOutObservable = Flowable.create<Boolean>(
                { emitter ->
                    firebaseAuth.addAuthStateListener {
                        if (this.signedOut) {
                            emitter.onNext(true)
                        }
                    }
                },
                BackpressureStrategy.BUFFER
        )
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
    override val authProvider: AuthProviders
        get() = when (user!!.providerData[1].providerId) {
            GoogleAuthProvider.PROVIDER_ID -> AuthProviders.GOOGLE
            EmailAuthProvider.PROVIDER_ID -> AuthProviders.EMAIL
            PhoneAuthProvider.PROVIDER_ID -> AuthProviders.PHONE
            else -> AuthProviders.UNKNOWN
        }

    override fun sendVerificationEmail() = createCompletable { emitter ->
        user?.sendEmailVerification(actionCodeSettings)
                ?.addOnSuccessListener { emitter.onComplete() }
                ?.addOnFailureListener { emitter.onError(it) }
                ?: emitter.onError(userNullException)
    }

    override fun reloadUser() = createCompletable { emitter ->
        user?.getIdToken(true)
                ?.addOnSuccessListener { reload(emitter) }
                ?.addOnFailureListener { emitter.onError(it) }
                ?: emitter.onError(userNullException)
    }

    private fun reload(emitter: CompletableEmitter) {
        user?.reload()
                ?.addOnSuccessListener { emitter.onComplete() }
                ?.addOnFailureListener { emitter.onError(it) }
                ?: emitter.onError(userNullException)
    }


    override fun signOut() = createCompletable { emitter ->
        authUI.signOut(application)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
    }


    override fun validatePhoneNumber(phoneNum: String) = Single.create<PhoneNumValidationResults> { emitter ->
        PhoneAuthProvider.getInstance(firebaseAuth).verifyPhoneNumber(
                formatPhoneNum(phoneNum),
                SMS_TIME_OUT_SECONDS,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                validateNumberCallbacks(emitter)
        )
    }

    private fun formatPhoneNum(phoneNum: String) = "$PHONE_NUM_COUNTRY_CODE_USA$phoneNum"

    private fun validateNumberCallbacks(emitter: SingleEmitter<PhoneNumValidationResults>) = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            emitter.onSuccess(PhoneNumValidationResults.Validated)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            emitter.onError(e)
        }

        override fun onCodeSent(validationCode: String, token: PhoneAuthProvider.ForceResendingToken) {
            emitter.onSuccess(PhoneNumValidationResults.SmsSent(validationCode))
        }
    }


    override fun deleteGoogleUser() = createCompletable { emitter ->
        deleteAccount(
                GoogleAuthProvider.getCredential(
                        GoogleSignIn.getLastSignedInAccount(application)!!.idToken,
                        null),
                emitter
        )
    }

    override fun deleteEmailUser(password: String) = createCompletable { emitter ->
        deleteAccount(
                EmailAuthProvider.getCredential(email!!, password),
                emitter
        )
    }

    override fun deletePhoneUser(verificationId: String,
                                 smsCode: String) = createCompletable { emitter ->
        deleteAccount(
                PhoneAuthProvider.getCredential(verificationId, smsCode),
                emitter
        )
    }

    private fun deleteAccount(authCredential: AuthCredential,
                              emitter: CompletableEmitter) {
        user!!.reauthenticate(authCredential)
                .addOnSuccessListener {
                    authUI.delete(application)
                            .addOnSuccessListener { emitter.onComplete() }
                            .addOnFailureListener { emitter.onError(it) }
                }.addOnFailureListener { emitter.onError(it) }
    }


    override fun userSignedOutObservable() = userSignedOutObservable
}

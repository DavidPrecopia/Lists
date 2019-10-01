package com.example.david.lists.view.authentication

import com.google.firebase.auth.FirebaseUser

interface IAuthContract {
    interface View {
        fun signIn(requestCode: Int)

        fun signOut()

        fun sendEmailVerification(user: FirebaseUser)

        fun displayEmailSentMessage(email: String)

        fun displayMessage(message: String)

        fun openMainActivity(requestCode: Int)

        fun finishView()
    }

    interface Logic {
        fun onStart()

        fun onActivityResult(resultCode: Int)

        fun signInSuccessful()

        fun signInCancelled()

        fun sentEmailVerification()

        fun failedToSendEmailVerification(e: Exception)

        fun signInFailed(errorCode: Int)

        fun signOutSucceeded()

        fun signOutFailed(e: Exception)
    }

    interface ViewModel {
        val signInRequestCode: Int

        val mainActivityRequestCode: Int

        var emailVerificationSent: Boolean

        val msgSignInSucceed: String

        val msgSignInCanceled: String

        val msgSignInError: String

        fun getMsgSignInError(errorCode: Int): String

        val msgEmailNotVerified: String

        val msgSignOutSucceed: String

        val msgSignOutFailed: String
    }

    companion object ResultCode {
        const val FINISH = 5000
        const val SIGN_OUT = 5002
    }
}

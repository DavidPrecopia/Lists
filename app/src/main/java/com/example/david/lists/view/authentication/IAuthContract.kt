package com.example.david.lists.view.authentication

interface IAuthContract {
    interface View {
        fun signIn(requestCode: Int)

        fun signOut()

        fun displayEmailSentMessage(email: String)

        fun hideEmailSentMessage()

        fun displayMessage(message: String)

        fun openMainView()

        fun finishView()
    }

    interface Logic {
        fun onStart(signOut: Boolean = false)

        fun signInSuccessful()

        fun signInCancelled()

        fun signInFailed(errorCode: Int)

        fun signOutSucceeded()

        fun signOutFailed(e: Exception)

        fun verifyEmailButtonClicked()
    }

    interface ViewModel {
        val signInRequestCode: Int

        var emailVerificationSent: Boolean

        val msgSignInSucceed: String

        val msgSignInCanceled: String

        val msgSignInError: String

        fun getMsgSignInError(errorCode: Int): String

        val msgEmailNotVerified: String

        val msgSignOutSucceed: String

        val msgSignOutFailed: String
    }
}

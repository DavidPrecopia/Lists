package com.example.david.lists.view.authentication

interface IAuthContract {
    interface View {
        fun signIn(requestCode: Int)

        fun signOut()

        fun displayMessage(message: String)

        fun setResult(result: AuthResult)

        fun setResultFailed(reason: String)

        fun finishView()
    }

    interface Logic {
        fun onStart(authGoal: AuthGoal)

        fun signInSuccessful()

        fun signInCancelled()

        fun signInFailed(errorCode: Int)

        fun signOutSucceeded()

        fun signOutFailed(e: Exception)
    }

    interface ViewModel {
        val requestCode: Int

        val msgSignInSucceed: String

        val msgSignInCanceled: String

        val msgSignInWhenNotAnon: String

        fun getMsgSignInError(errorCode: Int): String

        val msgSignOutSucceed: String

        val msgSignOutFailed: String
    }

    enum class AuthResult {
        AUTH_SUCCESS,
        AUTH_FAILED,
        // the user cancelled.
        AUTH_CANCELLED
    }

    enum class AuthGoal {
        SIGN_IN,
        SIGN_OUT,
        AUTH_ANON
    }
}

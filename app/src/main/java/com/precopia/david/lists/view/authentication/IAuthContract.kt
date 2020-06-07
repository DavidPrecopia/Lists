package com.precopia.david.lists.view.authentication

import androidx.lifecycle.LiveData

interface IAuthContract {
    interface View

    interface Logic {
        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
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


    sealed class ViewEvents {
        data class SignIn(val requestCode: Int) : ViewEvents()
        data class DisplayEmailSentMessage(val email: String) : ViewEvents()
        object HideEmailSentMessage : ViewEvents()
        data class DisplayMessage(val message: String) : ViewEvents()
        object OpenMainView : ViewEvents()
        object FinishView : ViewEvents()
    }

    sealed class LogicEvents {
        data class OnStart(val signOut: Boolean = false) : LogicEvents()
        object SignInSuccessful : LogicEvents()
        object SignInCancelled : LogicEvents()
        data class SignInFailed(val errorCode: Int) : LogicEvents()
        object VerifyEmailButtonClicked : LogicEvents()
    }
}

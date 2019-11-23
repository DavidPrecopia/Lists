package com.example.david.lists.view.reauthentication.phone

interface IPhoneReAuthContract {
    interface View {
        fun displayMessage(message: String)

        fun displayError(message: String)

        fun displayLoading()

        fun hideLoading()

        fun openSmsVerification(phoneNum: String, verificationId: String)

        fun finishView()
    }

    interface Logic {
        fun onEvent(event: ViewEvent)
    }

    interface ViewModel {
        var phoneNumber: String

        val msgSmsSent: String

        val msgInvalidNum: String

        val msgTryAgainLater: String

        val msgGenericError: String

        val msgTooManyRequest: String
    }

    sealed class ViewEvent {
        data class ConfirmPhoneNumClicked(val phoneNum: String) : ViewEvent()
    }
}
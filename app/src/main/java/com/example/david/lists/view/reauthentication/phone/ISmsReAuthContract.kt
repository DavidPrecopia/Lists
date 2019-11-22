package com.example.david.lists.view.reauthentication.phone

interface ISmsReAuthContract {
    interface View {
        fun startTimer(durationSeconds: Long)

        fun cancelTimer()

        fun displayMessage(message: String)

        fun displayError(message: String)

        fun displayLoading()

        fun hideLoading()

        fun openAuthView()

        fun finishView()
    }

    interface Logic {
        fun onEvent(event: ViewEvent)
    }

    interface ViewModel {
        var phoneNumber: String

        var verificationId: String

        val msgGenericError: String

        val msgTryAgainLater: String

        val msgInvalidSms: String

        val msgReEnterSms: String

        val msgSmsSent: String

        val msgAccountDeletionSucceed: String

        val msgAccountDeletionFailed: String
    }

    sealed class ViewEvent {
        data class OnStart(val phoneNum: String, val verificationId: String): ViewEvent()
        data class ConfirmSmsClicked(val sms: String) : ViewEvent()
        object TimerFinished: ViewEvent()
    }
}
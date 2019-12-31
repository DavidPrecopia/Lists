package com.precopia.david.lists.view.reauthentication.phone

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

        val msgSmsSent: String

        val msgTooManyRequest: String

        val msgAccountDeletionSucceed: String

        val msgAccountDeletionFailed: String
    }

    sealed class ViewEvent {
        data class OnStart(val phoneNum: String, val verificationId: String, val timeLeft: Long) : ViewEvent()
        data class ConfirmSmsClicked(val sms: String) : ViewEvent()
        object ViewDestroyed : ViewEvent()
        object TimerFinished : ViewEvent()
    }
}
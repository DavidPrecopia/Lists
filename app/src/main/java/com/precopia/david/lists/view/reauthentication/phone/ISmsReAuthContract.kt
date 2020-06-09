package com.precopia.david.lists.view.reauthentication.phone

import androidx.lifecycle.LiveData

interface ISmsReAuthContract {
    interface View

    interface Logic {
        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
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


    sealed class ViewEvents {
        data class StartTimer(val durationSeconds: Long) : ViewEvents()
        object CancelTimer : ViewEvents()
        data class DisplayMessage(val message: String) : ViewEvents()
        data class DisplayError(val message: String) : ViewEvents()
        object DisplayLoading : ViewEvents()
        object HideLoading : ViewEvents()
        object OpenAuthView : ViewEvents()
        object FinishView : ViewEvents()
    }

    sealed class LogicEvents {
        data class OnStart(val phoneNum: String, val verificationId: String, val timeLeft: Long) : LogicEvents()
        data class ConfirmSmsClicked(val sms: String) : LogicEvents()
        object ViewDestroyed : LogicEvents()
        object TimerFinished : LogicEvents()
    }
}
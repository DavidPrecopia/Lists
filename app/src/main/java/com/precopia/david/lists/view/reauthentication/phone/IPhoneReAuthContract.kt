package com.precopia.david.lists.view.reauthentication.phone

import androidx.lifecycle.LiveData

interface IPhoneReAuthContract {
    interface View

    interface Logic {
        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
    }

    interface ViewModel {
        var phoneNumber: String

        val msgSmsSent: String

        val msgInvalidNum: String

        val msgTryAgainLater: String

        val msgGenericError: String

        val msgTooManyRequest: String
    }


    sealed class ViewEvents {
        data class DisplayMessage(val message: String) : ViewEvents()
        data class DisplayError(val message: String) : ViewEvents()
        object DisplayLoading : ViewEvents()
        object HideLoading : ViewEvents()
        data class OpenSmsVerification(val phoneNum: String, val verificationId: String) : ViewEvents()
        object FinishView : ViewEvents()
    }

    sealed class LogicEvents {
        data class ConfirmPhoneNumClicked(val phoneNum: String) : LogicEvents()
    }
}
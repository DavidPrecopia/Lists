package com.precopia.david.lists.view.reauthentication.email

import androidx.lifecycle.LiveData

interface IEmailReAuthContract {
    interface View

    interface Logic {
        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
    }

    interface ViewModel {
        val msgInvalidPassword: String

        val msgTooManyRequest: String

        val msgAccountDeletionSucceed: String

        val msgAccountDeletionFailed: String
    }


    sealed class ViewEvents {
        object OpenAuthView : ViewEvents()
        data class DisplayMessage(val message: String) : ViewEvents()
        data class DisplayError(val message: String) : ViewEvents()
        object DisplayLoading : ViewEvents()
        object HideLoading : ViewEvents()
        object FinishView : ViewEvents()
    }

    sealed class LogicEvents {
        data class DeleteAcctClicked(val password: String) : LogicEvents()
    }
}
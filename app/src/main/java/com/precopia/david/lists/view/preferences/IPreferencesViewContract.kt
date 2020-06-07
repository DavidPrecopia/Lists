package com.precopia.david.lists.view.preferences

import androidx.lifecycle.LiveData

interface IPreferencesViewContract {
    interface View

    interface Logic {
        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
    }

    interface ViewModel {
        val msgDeletionFailed: String
    }


    sealed class ViewEvents {
        object ConfirmSignOut : ViewEvents()
        object ConfirmAccountDeletion : ViewEvents()
        object OpenGoogleReAuth : ViewEvents()
        object OpenEmailReAuth : ViewEvents()
        object OpenPhoneReAuth : ViewEvents()
        data class DisplayMessage(val message: String) : ViewEvents()
    }

    sealed class LogicEvents {
        object SignOutClicked : LogicEvents()
        object DeleteAccountClicked : LogicEvents()
        object DeleteAccountConfirmed : LogicEvents()

    }
}
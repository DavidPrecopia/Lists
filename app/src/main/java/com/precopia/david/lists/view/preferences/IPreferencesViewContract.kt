package com.precopia.david.lists.view.preferences

interface IPreferencesViewContract {
    interface View {
        fun confirmSignOut()

        fun confirmAccountDeletion()

        fun openGoogleReAuth()

        fun openEmailReAuth()

        fun openPhoneReAuth()

        fun displayMessage(message: String)
    }

    interface Logic {
        fun onEvent(viewEvent: ViewEvent)
    }

    interface ViewModel {
        val msgDeletionFailed: String
    }

    sealed class ViewEvent {
        object SignOutClicked : ViewEvent()
        object DeleteAccountClicked : ViewEvent()
        object DeleteAccountConfirmed : ViewEvent()
    }
}
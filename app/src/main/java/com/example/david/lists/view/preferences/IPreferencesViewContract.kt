package com.example.david.lists.view.preferences

interface IPreferencesViewContract {
    interface View {
        fun confirmSignOut()

        fun confirmAccountDeletion()
    }

    interface Logic {
        fun onEvent(viewEvent: ViewEvent)
    }

    sealed class ViewEvent {
        object SignOutClicked : ViewEvent()
        object DeleteAccountClicked : ViewEvent()
    }
}
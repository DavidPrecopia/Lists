package com.example.david.lists.view.preferences

import com.example.david.lists.view.preferences.IPreferencesViewContract.ViewEvent

class PreferencesLogic(val view: IPreferencesViewContract.View) : IPreferencesViewContract.Logic {
    override fun onEvent(viewEvent: ViewEvent) {
        when (viewEvent) {
            ViewEvent.SignOutClicked -> view.confirmSignOut()
            ViewEvent.DeleteAccountClicked -> view.confirmAccountDeletion()
        }
    }
}
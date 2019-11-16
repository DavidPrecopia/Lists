package com.example.david.lists.view.preferences

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.data.repository.IRepositoryContract.Providers
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.preferences.IPreferencesViewContract.ViewEvent

class PreferencesLogic(private val view: IPreferencesViewContract.View,
                       private val viewModel: IPreferencesViewContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository) :
        IPreferencesViewContract.Logic {
    override fun onEvent(viewEvent: ViewEvent) {
        when (viewEvent) {
            ViewEvent.SignOutClicked -> view.confirmSignOut()
            ViewEvent.DeleteAccountClicked -> view.confirmAccountDeletion()
            ViewEvent.DeleteAccountConfirmed -> deleteAccount()
        }
    }

    private fun deleteAccount() {
        when (userRepo.authProvider) {
            Providers.GOOGLE -> view.openGoogleReAuth()
            Providers.EMAIL -> view.openEmailReAuth()
            Providers.PHONE -> view.openPhoneReAuth()
            Providers.UNKNOWN -> {
                view.displayMessage(viewModel.msgDeletionFailed)
                UtilExceptions.throwException(IllegalStateException("Unknown authentication provider"))
            }
        }
    }
}
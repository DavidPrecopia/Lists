package com.example.david.lists.view.preferences

import com.example.androiddata.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.preferences.IPreferencesViewContract.ViewEvent
import com.example.domain.constants.AuthProviders

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
            AuthProviders.GOOGLE -> view.openGoogleReAuth()
            AuthProviders.EMAIL -> view.openEmailReAuth()
            AuthProviders.PHONE -> view.openPhoneReAuth()
            AuthProviders.UNKNOWN -> {
                view.displayMessage(viewModel.msgDeletionFailed)
                UtilExceptions.throwException(IllegalStateException("Unknown authentication provider"))
            }
        }
    }
}
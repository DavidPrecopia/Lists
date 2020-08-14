package com.precopia.david.lists.view.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.precopia.david.lists.util.IUtilThemeContract
import com.precopia.david.lists.util.IUtilThemeContract.ThemeValues
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.LogicEvents
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.ViewEvents
import com.precopia.domain.constants.AuthProviders
import com.precopia.domain.repository.IRepositoryContract

class PreferencesLogic(private val viewModel: IPreferencesViewContract.ViewModel,
                       private val utilTheme: IUtilThemeContract,
                       private val userRepo: IRepositoryContract.UserRepository) :
        ViewModel(),
        IPreferencesViewContract.Logic {

    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(event: LogicEvents) {
        when (event) {
            is LogicEvents.ThemeChanged -> themeChanged(event.value)
            LogicEvents.SignOutClicked -> viewEventLiveData.value =
                    ViewEvents.ConfirmSignOut
            LogicEvents.DeleteAccountClicked -> viewEventLiveData.value =
                    ViewEvents.ConfirmAccountDeletion
            LogicEvents.DeleteAccountConfirmed -> deleteAccount()
        }
    }


    private fun themeChanged(value: String) {
        when (value) {
            ThemeValues.DAY.value -> utilTheme.setDay()
            ThemeValues.DARK.value -> utilTheme.setDark()
            ThemeValues.FOLLOW_SYSTEM.value -> utilTheme.setFollowSystem()
        }
    }


    private fun deleteAccount() {
        when (userRepo.authProvider) {
            AuthProviders.GOOGLE -> viewEventLiveData.value =
                    ViewEvents.OpenGoogleReAuth
            AuthProviders.EMAIL -> viewEventLiveData.value =
                    ViewEvents.OpenEmailReAuth
            AuthProviders.PHONE -> viewEventLiveData.value =
                    ViewEvents.OpenPhoneReAuth
            AuthProviders.UNKNOWN -> {
                viewEventLiveData.value = ViewEvents.DisplayMessage(viewModel.msgDeletionFailed)
                UtilExceptions.throwException(IllegalStateException("Unknown authentication provider"))
            }
        }
    }


    override fun observe(): LiveData<ViewEvents> = viewEventLiveData
}
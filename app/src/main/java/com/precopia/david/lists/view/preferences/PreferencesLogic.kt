package com.precopia.david.lists.view.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.precopia.david.lists.util.IUtilThemeContract
import com.precopia.david.lists.util.IUtilThemeContract.ThemeLabels
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.LogicEvents
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.ViewEvents
import com.precopia.domain.constants.AuthProviders
import com.precopia.domain.repository.IRepositoryContract

private const val SELECTED_THEME_INDEX_KEY = "selected_index_key"

class PreferencesLogic(private val viewModel: IPreferencesViewContract.ViewModel,
                       private val utilTheme: IUtilThemeContract,
                       private val userRepo: IRepositoryContract.UserRepository,
                       private val sharedPrefs: SharedPreferences) :
        ViewModel(),
        IPreferencesViewContract.Logic {

    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(event: LogicEvents) {
        when (event) {
            LogicEvents.ThemeClicked -> viewEvent(ViewEvents.OpenThemeSelector(getSelectedIndex()))
            is LogicEvents.ThemeChanged -> themeChanged(event.label, event.selectedIndex)
            LogicEvents.SignOutClicked -> viewEvent(ViewEvents.ConfirmSignOut)
            LogicEvents.DeleteAccountClicked -> viewEvent(ViewEvents.ConfirmAccountDeletion)
            LogicEvents.DeleteAccountConfirmed -> deleteAccount()
        }
    }


    private fun themeChanged(value: ThemeLabels, selectedIndex: Int) {
        savedSelectedIndex(selectedIndex)
        when (value) {
            ThemeLabels.DAY -> utilTheme.setDay()
            ThemeLabels.DARK -> utilTheme.setDark()
            ThemeLabels.FOLLOW_SYSTEM -> utilTheme.setFollowSystem()
        }
        viewEvent(ViewEvents.ClearLiveData)
    }


    private fun deleteAccount() {
        when (userRepo.authProvider) {
            AuthProviders.GOOGLE -> viewEvent(ViewEvents.OpenGoogleReAuth)
            AuthProviders.EMAIL -> viewEvent(ViewEvents.OpenEmailReAuth)
            AuthProviders.PHONE -> viewEvent(ViewEvents.OpenPhoneReAuth)
            AuthProviders.UNKNOWN -> {
                viewEvent(ViewEvents.DisplayMessage(viewModel.msgDeletionFailed))
                UtilExceptions.throwException(IllegalStateException("Unknown authentication provider"))
            }
        }
    }


    override fun observe(): LiveData<ViewEvents> = viewEventLiveData


    private fun viewEvent(event: ViewEvents) {
        viewEventLiveData.value = event
    }

    private fun getSelectedIndex() = sharedPrefs.getInt(SELECTED_THEME_INDEX_KEY, -1)

    private fun savedSelectedIndex(position: Int) {
        sharedPrefs.edit { putInt(SELECTED_THEME_INDEX_KEY, position) }
    }
}
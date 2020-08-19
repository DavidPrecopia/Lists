package com.precopia.david.lists.view.preferences

import androidx.lifecycle.LiveData
import com.precopia.david.lists.util.IUtilThemeContract.ThemeLabels

interface IPreferencesViewContract {
    interface View

    interface Logic {
        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
    }

    interface ViewModel {
        val msgDeletionFailed: String
    }


    /**
     * ATTENTION: [ViewEvents.ClearLiveData] is a workaround of LiveData re-sending its
     * last held value post a configuration change.
     * When the theme is changed, the parent Activity is re-created, this causes
     * [ViewEvents.OpenThemeSelector] to be re-sent, thus the theme selector reappears
     * after the user has selected a theme.
     */
    sealed class ViewEvents {
        data class OpenThemeSelector(val selectedIndex: Int) : ViewEvents()
        object ClearLiveData: ViewEvents()
        object ConfirmSignOut : ViewEvents()
        object ConfirmAccountDeletion : ViewEvents()
        object OpenGoogleReAuth : ViewEvents()
        object OpenEmailReAuth : ViewEvents()
        object OpenPhoneReAuth : ViewEvents()
        data class DisplayMessage(val message: String) : ViewEvents()
    }

    sealed class LogicEvents {
        object ThemeClicked : LogicEvents()
        data class ThemeChanged(val label: ThemeLabels, val selectedIndex: Int) : LogicEvents()
        object SignOutClicked : LogicEvents()
        object DeleteAccountClicked : LogicEvents()
        object DeleteAccountConfirmed : LogicEvents()
    }
}
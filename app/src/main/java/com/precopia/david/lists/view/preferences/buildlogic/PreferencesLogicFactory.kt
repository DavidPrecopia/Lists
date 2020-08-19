package com.precopia.david.lists.view.preferences.buildlogic

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.IUtilThemeContract
import com.precopia.david.lists.view.preferences.IPreferencesViewContract
import com.precopia.david.lists.view.preferences.PreferencesLogic
import com.precopia.domain.repository.IRepositoryContract

class PreferencesLogicFactory(
        private val viewModel: IPreferencesViewContract.ViewModel,
        private val utilTheme: IUtilThemeContract,
        private val userRepo: IRepositoryContract.UserRepository,
        private val sharedPrefs: SharedPreferences
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PreferencesLogic(viewModel, utilTheme, userRepo, sharedPrefs) as T
    }
}
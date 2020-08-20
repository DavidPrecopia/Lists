package com.precopia.david.lists.view.preferences.buildlogic

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.IUtilThemeContract
import com.precopia.david.lists.view.preferences.IPreferencesViewContract
import com.precopia.david.lists.view.preferences.PreferencesLogic
import com.precopia.david.lists.view.preferences.PreferencesViewModel
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class PreferencesModule {
    @ViewScope
    @Provides
    fun logic(view: Fragment,
              factory: ViewModelProvider.NewInstanceFactory): IPreferencesViewContract.Logic {
        return ViewModelProvider(view, factory).get(PreferencesLogic::class.java)
    }

    @ViewScope
    @Provides
    fun factory(viewModel: IPreferencesViewContract.ViewModel,
                utilTheme: IUtilThemeContract,
                userRepo: IRepositoryContract.UserRepository,
                sharedPrefs: SharedPreferences): ViewModelProvider.NewInstanceFactory {
        return PreferencesLogicFactory(viewModel, utilTheme, userRepo, sharedPrefs)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IPreferencesViewContract.ViewModel {
        return PreferencesViewModel(getStringRes)
    }
}
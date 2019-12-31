package com.precopia.david.lists.view.preferences.buildlogic

import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.preferences.IPreferencesViewContract
import com.precopia.david.lists.view.preferences.PreferencesLogic
import com.precopia.david.lists.view.preferences.PreferencesViewModel
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class PreferencesViewModule {
    @ViewScope
    @Provides
    fun logic(view: IPreferencesViewContract.View,
              viewModel: IPreferencesViewContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository): IPreferencesViewContract.Logic {
        return PreferencesLogic(view, viewModel, userRepo)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IPreferencesViewContract.ViewModel {
        return PreferencesViewModel(getStringRes)
    }
}
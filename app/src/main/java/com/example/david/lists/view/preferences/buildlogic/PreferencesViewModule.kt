package com.example.david.lists.view.preferences.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.preferences.IPreferencesViewContract
import com.example.david.lists.view.preferences.PreferencesLogic
import com.example.david.lists.view.preferences.PreferencesViewModel
import com.example.domain.repository.IRepositoryContract
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

    @ViewScope
    @Provides
    fun viewModel(application: Application): IPreferencesViewContract.ViewModel {
        return PreferencesViewModel(application)
    }
}
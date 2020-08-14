package com.precopia.david.lists.view.preferences.buildlogic

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.IUtilNightModeContract
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
                utilNightMode: IUtilNightModeContract,
                userRepo: IRepositoryContract.UserRepository): ViewModelProvider.NewInstanceFactory {
        return PreferencesLogicFactory(viewModel, utilNightMode, userRepo)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IPreferencesViewContract.ViewModel {
        return PreferencesViewModel(getStringRes)
    }
}
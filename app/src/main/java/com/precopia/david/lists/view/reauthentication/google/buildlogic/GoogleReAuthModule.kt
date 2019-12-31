package com.precopia.david.lists.view.reauthentication.google.buildlogic

import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.google.GoogleReAuthLogic
import com.precopia.david.lists.view.reauthentication.google.GoogleReAuthViewModel
import com.precopia.david.lists.view.reauthentication.google.IGoogleReAuthContract
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class GoogleReAuthModule {
    @ViewScope
    @Provides
    fun logic(view: IGoogleReAuthContract.View,
              viewModel: IGoogleReAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository,
              schedulerProvider: ISchedulerProviderContract): IGoogleReAuthContract.Logic {
        return GoogleReAuthLogic(view, viewModel, userRepo, schedulerProvider)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IGoogleReAuthContract.ViewModel {
        return GoogleReAuthViewModel(getStringRes)
    }
}
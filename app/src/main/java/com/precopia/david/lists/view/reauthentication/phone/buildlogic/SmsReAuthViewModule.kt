package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.ISmsReAuthContract
import com.precopia.david.lists.view.reauthentication.phone.SmsReAuthLogic
import com.precopia.david.lists.view.reauthentication.phone.SmsReAuthViewModel
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class SmsReAuthViewModule {
    @ViewScope
    @Provides
    fun logic(view: ISmsReAuthContract.View,
              viewModel: ISmsReAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository,
              schedulerProvider: ISchedulerProviderContract): ISmsReAuthContract.Logic {
        return SmsReAuthLogic(view, viewModel, userRepo, schedulerProvider)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): ISmsReAuthContract.ViewModel {
        return SmsReAuthViewModel(getStringRes)
    }
}
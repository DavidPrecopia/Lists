package com.example.david.lists.view.reauthentication.phone.buildlogic

import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.reauthentication.phone.ISmsReAuthContract
import com.example.david.lists.view.reauthentication.phone.SmsReAuthLogic
import com.example.david.lists.view.reauthentication.phone.SmsReAuthViewModel
import com.example.domain.repository.IRepositoryContract
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
package com.example.david.lists.view.reauthentication.phone.buildlogic

import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.reauthentication.phone.IPhoneReAuthContract
import com.example.david.lists.view.reauthentication.phone.PhoneReAuthLogic
import com.example.david.lists.view.reauthentication.phone.PhoneReAuthViewModel
import com.example.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class PhoneReAuthViewModule {
    @ViewScope
    @Provides
    fun logic(view: IPhoneReAuthContract.View,
              viewModel: IPhoneReAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository,
              schedulerProvider: ISchedulerProviderContract): IPhoneReAuthContract.Logic {
        return PhoneReAuthLogic(view, viewModel, userRepo, schedulerProvider)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IPhoneReAuthContract.ViewModel {
        return PhoneReAuthViewModel(getStringRes)
    }
}

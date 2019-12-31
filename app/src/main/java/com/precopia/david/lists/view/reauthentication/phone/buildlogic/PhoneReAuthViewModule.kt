package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract
import com.precopia.david.lists.view.reauthentication.phone.PhoneReAuthLogic
import com.precopia.david.lists.view.reauthentication.phone.PhoneReAuthViewModel
import com.precopia.domain.repository.IRepositoryContract
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

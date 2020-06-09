package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.ISmsReAuthContract
import com.precopia.david.lists.view.reauthentication.phone.SmsReAuthLogic
import com.precopia.david.lists.view.reauthentication.phone.SmsReAuthViewModel
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class SmsReAuthModule {
    @ViewScope
    @Provides
    fun logic(view: Fragment,
              factory: ViewModelProvider.NewInstanceFactory): ISmsReAuthContract.Logic {
        return ViewModelProvider(view, factory).get(SmsReAuthLogic::class.java)
    }

    @ViewScope
    @Provides
    fun factory(viewModel: ISmsReAuthContract.ViewModel,
                userRepo: IRepositoryContract.UserRepository,
                schedulerProvider: ISchedulerProviderContract): ViewModelProvider.NewInstanceFactory {
        return SmsReAuthLogicFactory(viewModel, userRepo, schedulerProvider)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): ISmsReAuthContract.ViewModel {
        return SmsReAuthViewModel(getStringRes)
    }
}
package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract
import com.precopia.david.lists.view.reauthentication.phone.PhoneReAuthLogic
import com.precopia.david.lists.view.reauthentication.phone.PhoneReAuthViewModel
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class PhoneReAuthModule {
    @ViewScope
    @Provides
    fun logic(view: Fragment,
              factory: ViewModelProvider.NewInstanceFactory): IPhoneReAuthContract.Logic {
        return ViewModelProvider(view, factory).get(PhoneReAuthLogic::class.java)
    }

    @ViewScope
    @Provides
    fun factory(viewModel: IPhoneReAuthContract.ViewModel,
                userRepo: IRepositoryContract.UserRepository,
                schedulerProvider: ISchedulerProviderContract): ViewModelProvider.NewInstanceFactory {
        return PhoneReAuthLogicFactory(viewModel, userRepo, schedulerProvider)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IPhoneReAuthContract.ViewModel {
        return PhoneReAuthViewModel(getStringRes)
    }
}

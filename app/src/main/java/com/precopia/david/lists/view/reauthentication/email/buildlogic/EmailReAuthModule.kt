package com.precopia.david.lists.view.reauthentication.email.buildlogic

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.email.EmailReAuthLogic
import com.precopia.david.lists.view.reauthentication.email.EmailReAuthViewModel
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class EmailReAuthModule {
    @ViewScope
    @Provides
    fun logic(view: Fragment,
              factory: ViewModelProvider.NewInstanceFactory): IEmailReAuthContract.Logic {
        return ViewModelProvider(view, factory).get(EmailReAuthLogic::class.java)
    }

    @ViewScope
    @Provides
    fun factory(viewModel: IEmailReAuthContract.ViewModel,
                userRepo: IRepositoryContract.UserRepository,
                schedulerProvider: ISchedulerProviderContract): ViewModelProvider.NewInstanceFactory {
        return EmailReAuthLogicFactory(viewModel, userRepo, schedulerProvider)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IEmailReAuthContract.ViewModel {
        return EmailReAuthViewModel(getStringRes)
    }
}
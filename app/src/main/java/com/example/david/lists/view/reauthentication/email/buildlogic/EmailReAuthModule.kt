package com.example.david.lists.view.reauthentication.email.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.reauthentication.email.EmailReAuthLogic
import com.example.david.lists.view.reauthentication.email.EmailReAuthViewModel
import com.example.david.lists.view.reauthentication.email.IEmailReAuthContract
import com.example.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
class EmailReAuthModule {
    @ViewScope
    @Provides
    fun logic(view: IEmailReAuthContract.View,
              viewModel: IEmailReAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository,
              schedulerProvider: ISchedulerProviderContract): IEmailReAuthContract.Logic {
        return EmailReAuthLogic(view, viewModel, userRepo, schedulerProvider)
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): IEmailReAuthContract.ViewModel {
        return EmailReAuthViewModel(application)
    }
}
package com.example.david.lists.view.reauthentication.google.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.reauthentication.google.GoogleReAuthLogic
import com.example.david.lists.view.reauthentication.google.GoogleReAuthViewModel
import com.example.david.lists.view.reauthentication.google.IGoogleReAuthContract
import com.example.domain.repository.IRepositoryContract
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

    @ViewScope
    @Provides
    fun viewModel(application: Application): IGoogleReAuthContract.ViewModel {
        return GoogleReAuthViewModel(application)
    }
}
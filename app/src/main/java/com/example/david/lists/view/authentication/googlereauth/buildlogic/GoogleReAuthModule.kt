package com.example.david.lists.view.authentication.googlereauth.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.authentication.googlereauth.GoogleReAuthLogic
import com.example.david.lists.view.authentication.googlereauth.GoogleReAuthViewModel
import com.example.david.lists.view.authentication.googlereauth.IGoogleReAuthContract
import dagger.Module
import dagger.Provides

@Module
class GoogleReAuthModule {
    @ViewScope
    @Provides
    fun logic(view: IGoogleReAuthContract.View,
              viewModel: IGoogleReAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository): IGoogleReAuthContract.Logic {
        return GoogleReAuthLogic(view, viewModel, userRepo)
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): IGoogleReAuthContract.ViewModel {
        return GoogleReAuthViewModel(application)
    }
}
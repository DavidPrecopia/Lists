package com.example.david.lists.view.reauthentication.phone.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewScope
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
              userRepo: IRepositoryContract.UserRepository): ISmsReAuthContract.Logic {
        return SmsReAuthLogic(view, viewModel, userRepo)
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): ISmsReAuthContract.ViewModel {
        return SmsReAuthViewModel(application)
    }
}
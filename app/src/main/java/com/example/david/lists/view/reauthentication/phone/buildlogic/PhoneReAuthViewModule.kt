package com.example.david.lists.view.reauthentication.phone.buildlogic

import android.app.Application
import com.example.androiddata.repository.IRepositoryContract
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.reauthentication.phone.IPhoneReAuthContract
import com.example.david.lists.view.reauthentication.phone.PhoneReAuthLogic
import com.example.david.lists.view.reauthentication.phone.PhoneReAuthViewModel
import dagger.Module
import dagger.Provides

@Module
class PhoneReAuthViewModule {
    @ViewScope
    @Provides
    fun logic(view: IPhoneReAuthContract.View,
              viewModel: IPhoneReAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository): IPhoneReAuthContract.Logic {
        return PhoneReAuthLogic(view, viewModel, userRepo)
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): IPhoneReAuthContract.ViewModel {
        return PhoneReAuthViewModel(application)
    }
}

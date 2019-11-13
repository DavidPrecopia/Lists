package com.example.david.lists.view.authentication.emailreauth.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.authentication.emailreauth.EmailReAuthLogic
import com.example.david.lists.view.authentication.emailreauth.EmailReAuthViewModel
import com.example.david.lists.view.authentication.emailreauth.IEmailReAuthContract
import dagger.Module
import dagger.Provides

@Module
class EmailReAuthModule {
    @ViewScope
    @Provides
    fun logic(view: IEmailReAuthContract.View,
              viewModel: IEmailReAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository): IEmailReAuthContract.Logic {
        return EmailReAuthLogic(view, viewModel, userRepo)
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): IEmailReAuthContract.ViewModel {
        return EmailReAuthViewModel(application)
    }
}
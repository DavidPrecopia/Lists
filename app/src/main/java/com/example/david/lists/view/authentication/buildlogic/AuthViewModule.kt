package com.example.david.lists.view.authentication.buildlogic

import android.app.Application
import android.content.Intent
import com.example.david.lists.R
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.authentication.AuthLogic
import com.example.david.lists.view.authentication.AuthViewModel
import com.example.david.lists.view.authentication.IAuthContract
import com.firebase.ui.auth.AuthUI
import dagger.Module
import dagger.Provides

@Module
internal class AuthViewModule {
    @ViewScope
    @Provides
    fun logic(view: IAuthContract.View,
              viewModel: IAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository): IAuthContract.Logic {
        return AuthLogic(view, viewModel, userRepo)
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): IAuthContract.ViewModel {
        return AuthViewModel(application, 101)
    }

    @ViewScope
    @Provides
    fun authUi(): AuthUI {
        return AuthUI.getInstance()
    }

    @ViewScope
    @Provides
    fun authIntent(): Intent {
        return authIntent
    }

    private val authIntent: Intent
        get() = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .enableAnonymousUsersAutoUpgrade()
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.mipmap.ic_launcher_round)
                .setTheme(R.style.FirebaseUIAuthStyle)
                .build()

    private val providers: List<AuthUI.IdpConfig>
        get() = listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.AnonymousBuilder().build()
        )
}

package com.example.david.lists.view.authentication.buildlogic

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import com.example.david.lists.R
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.authentication.AuthLogic
import com.example.david.lists.view.authentication.AuthViewModel
import com.example.david.lists.view.authentication.IAuthContract
import com.example.domain.repository.IRepositoryContract
import com.firebase.ui.auth.AuthUI
import dagger.Module
import dagger.Provides

@Module
internal class AuthViewModule {
    @ViewScope
    @Provides
    fun logic(view: IAuthContract.View,
              viewModel: IAuthContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository,
              schedulerProvider: ISchedulerProviderContract): IAuthContract.Logic {
        return AuthLogic(view, viewModel, userRepo, schedulerProvider)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String,
                  sharedPrefs: SharedPreferences,
                  application: Application): IAuthContract.ViewModel {
        return AuthViewModel(
                getStringRes,
                sharedPrefs,
                application.getString(R.string.email_verification_sent_shared_pref_key)
        )
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
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.mipmap.ic_launcher_round)
                .setTheme(R.style.FirebaseUIAuthStyle)
                .build()

    private val providers: List<AuthUI.IdpConfig>
        get() = listOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build()
        )
}

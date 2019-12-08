package com.example.androiddata.repository.buildlogic

import android.app.Application
import com.example.androiddata.repository.UserRepository
import com.example.domain.repository.IRepositoryContract
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

// This URL is irrelevant - using Google because it safe.
private const val CONTINUE_URL = "https://www.google.com/"
// The minimum versionCode that supports email verification
private const val MINIMUM_VERSION_CODE = "14"

@Module
class UserRepositoryModule {
    @Singleton
    @Provides
    fun userRepository(firebaseAuth: FirebaseAuth,
                       actionCodeSettings: ActionCodeSettings,
                       authUI: AuthUI,
                       application: Application): IRepositoryContract.UserRepository {
        return UserRepository(firebaseAuth, actionCodeSettings, authUI, application)
    }

    @Singleton
    @Provides
    fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun actionCodeSettings(application: Application): ActionCodeSettings {
        return ActionCodeSettings.newBuilder()
                .setUrl(CONTINUE_URL)
                .setAndroidPackageName(
                        application.packageName,
                        false,
                        MINIMUM_VERSION_CODE
                )
                .build()
    }

    @Singleton
    @Provides
    fun authUi(): AuthUI {
        return AuthUI.getInstance()
    }
}

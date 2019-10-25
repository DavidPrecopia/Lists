package com.example.david.lists.data.repository.buildlogic

import android.app.Application
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.data.repository.UserRepository
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
                       actionCodeSettings: ActionCodeSettings): IRepositoryContract.UserRepository {
        return UserRepository(firebaseAuth, actionCodeSettings)
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
}

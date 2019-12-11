package com.example.androiddata.repository.buildlogic

import android.app.Application
import com.example.androiddata.repository.UserRepository
import com.example.domain.repository.IRepositoryContract
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

// This URL is irrelevant - using Google because it safe.
private const val CONTINUE_URL = "https://www.google.com/"
// The minimum versionCode that supports email verification
private const val MINIMUM_VERSION_CODE = "14"

class UserRepositoryServiceLocator(application: Application, packageName: String) {

    private val userRepo: IRepositoryContract.UserRepository = UserRepository(
            firebaseAuth(),
            actionCodeSettings(packageName),
            authUi(),
            application
    )


    fun userRepository() = userRepo


    private fun firebaseAuth() = FirebaseAuth.getInstance()

    private fun actionCodeSettings(packageName: String) =
            ActionCodeSettings.newBuilder()
                    .setUrl(CONTINUE_URL)
                    .setAndroidPackageName(
                            packageName,
                            false,
                            MINIMUM_VERSION_CODE
                    )
                    .build()

    private fun authUi() = AuthUI.getInstance()
}


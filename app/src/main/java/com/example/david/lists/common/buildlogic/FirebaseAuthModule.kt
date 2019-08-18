package com.example.david.lists.common.buildlogic

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseAuthModule {
    @Singleton
    @Provides
    fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}

package com.example.david.lists.common.buildlogic;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
final class FirebaseAuthModule {
    @Singleton
    @Provides
    FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}

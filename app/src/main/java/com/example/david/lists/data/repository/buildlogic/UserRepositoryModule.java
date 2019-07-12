package com.example.david.lists.data.repository.buildlogic;

import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class UserRepositoryModule {
    @Singleton
    @Provides
    IRepositoryContract.UserRepository userRepository(FirebaseAuth firebaseAuth) {
        return new UserRepository(firebaseAuth);
    }
}

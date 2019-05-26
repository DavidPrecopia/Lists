package com.example.david.lists.di.data;

import com.example.david.lists.data.repository.IUserRepository;
import com.example.david.lists.data.repository.UserRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
final class UserRepositoryModule {
    @Singleton
    @Provides
    IUserRepository userRepository(FirebaseAuth firebaseAuth) {
        return new UserRepositoryImpl(firebaseAuth);
    }
}

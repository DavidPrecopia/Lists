package com.example.david.lists.data.repository.buildlogic

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserRepositoryModule {
    @Singleton
    @Provides
    fun userRepository(firebaseAuth: FirebaseAuth): IRepositoryContract.UserRepository {
        return UserRepository(firebaseAuth)
    }
}

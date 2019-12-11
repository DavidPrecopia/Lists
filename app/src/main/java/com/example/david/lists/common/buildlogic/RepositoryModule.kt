package com.example.david.lists.common.buildlogic

import android.app.Application
import com.example.androiddata.repository.buildlogic.RepositoryServiceLocator
import com.example.androiddata.repository.buildlogic.UserRepositoryServiceLocator
import com.example.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun userRepo(application: Application): IRepositoryContract.UserRepository {
        return UserRepositoryServiceLocator(application, application.packageName).userRepository()
    }

    @Singleton
    @Provides
    fun repo(userRepo: IRepositoryContract.UserRepository): IRepositoryContract.Repository {
        return RepositoryServiceLocator(userRepo).repository()
    }
}
package com.precopia.david.lists.common.buildlogic

import android.app.Application
import com.precopia.androiddata.repository.buildlogic.RepositoryServiceLocator
import com.precopia.androiddata.repository.buildlogic.UserRepositoryServiceLocator
import com.precopia.domain.repository.IRepositoryContract
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
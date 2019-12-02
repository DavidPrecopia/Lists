package com.example.androiddata.repository.buildlogic

import com.example.androiddata.remote.IRemoteRepositoryContract
import com.example.androiddata.repository.IRepositoryContract
import com.example.androiddata.repository.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun repository(remoteRepo: IRemoteRepositoryContract.Repository): IRepositoryContract.Repository {
        return Repository(remoteRepo)
    }
}

package com.example.androiddata.repository.buildlogic

import com.example.androiddata.remote.IRemoteRepositoryContract
import com.example.androiddata.remote.buildlogic.RemoteRepositoryModule
import com.example.androiddata.repository.Repository
import com.example.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [
    RemoteRepositoryModule::class
])
class RepositoryModule {
    @Singleton
    @Provides
    fun repository(remoteRepo: IRemoteRepositoryContract.Repository): IRepositoryContract.Repository {
        return Repository(remoteRepo)
    }
}

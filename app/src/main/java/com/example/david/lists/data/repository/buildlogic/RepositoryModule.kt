package com.example.david.lists.data.repository.buildlogic

import com.example.david.lists.data.remote.IRemoteRepositoryContract
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.data.repository.Repository
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

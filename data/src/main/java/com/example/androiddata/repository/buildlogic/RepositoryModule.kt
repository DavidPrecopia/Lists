package com.example.androiddata.repository.buildlogic

import com.example.androiddata.common.DataScope
import com.example.androiddata.remote.IRemoteRepositoryContract
import com.example.androiddata.repository.Repository
import com.example.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides

@Module
internal class RepositoryModule {
    @DataScope
    @Provides
    fun repository(remoteRepo: IRemoteRepositoryContract.Repository): IRepositoryContract.Repository {
        return Repository(remoteRepo)
    }
}

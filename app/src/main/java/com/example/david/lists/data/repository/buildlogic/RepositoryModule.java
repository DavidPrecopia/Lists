package com.example.david.lists.data.repository.buildlogic;

import com.example.david.lists.data.remote.IRemoteRepositoryContract;
import com.example.david.lists.data.repository.IRepositoryContract;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {
    @Singleton
    @Provides
    IRepositoryContract.Repository repository(IRemoteRepositoryContract.Repository remoteRepository) {
        return new com.example.david.lists.data.repository.Repository(remoteRepository);
    }
}

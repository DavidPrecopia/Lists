package com.example.david.lists.di.data;

import com.example.david.lists.data.remote.IRemoteRepository;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.data.repository.RepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = RemoteRepositoryModule.class)
class RepositoryModule {
    @Singleton
    @Provides
    IRepository model(IRemoteRepository remoteRepository) {
        return new RepositoryImpl(remoteRepository);
    }
}

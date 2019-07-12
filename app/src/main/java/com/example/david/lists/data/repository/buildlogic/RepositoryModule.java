package com.example.david.lists.data.repository.buildlogic;

import com.example.david.lists.data.remote.IRemoteRepository;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.data.repository.RepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {
    @Singleton
    @Provides
    IRepository repository(IRemoteRepository remoteRepository) {
        return new RepositoryImpl(remoteRepository);
    }
}

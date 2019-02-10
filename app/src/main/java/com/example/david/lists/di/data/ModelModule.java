package com.example.david.lists.di.data;

import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.data.model.Model;
import com.example.david.lists.data.remote.IRemoteDatabaseContract;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = RemoteDatabaseModule.class)
class ModelModule {
    @Singleton
    @Provides
    IModelContract model(IRemoteDatabaseContract remoteDatabase) {
        return new Model(remoteDatabase);
    }
}

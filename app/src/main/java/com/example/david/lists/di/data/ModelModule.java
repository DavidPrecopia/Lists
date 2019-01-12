package com.example.david.lists.di.data;

import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.data.model.Model;
import com.example.david.lists.data.remote.IRemoteStorageContract;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = RemoteStorageModule.class)
class ModelModule {
    @Singleton
    @Provides
    IModelContract modelContract(Model model) {
        return model;
    }

    @Singleton
    @Provides
    Model model(IRemoteStorageContract remoteStorage) {
        return new Model(remoteStorage);
    }
}

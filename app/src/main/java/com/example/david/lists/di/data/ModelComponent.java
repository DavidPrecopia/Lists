package com.example.david.lists.di.data;

import com.example.david.lists.data.model.IModelContract;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ModelModule.class)
public interface ModelComponent {
    IModelContract getModel();
}

package com.example.david.lists.application;

import android.app.Application;

import com.example.david.lists.di.data.ModelComponent;

public final class MyApplication extends Application {

    private ModelComponent modelComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        modelComponent = new InitDebug().init(this).getModelComponent();
    }

    public ModelComponent getModelComponent() {
        return this.modelComponent;
    }
}
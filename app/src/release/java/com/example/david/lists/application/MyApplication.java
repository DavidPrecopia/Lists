package com.example.david.lists.application;

import android.app.Application;

import com.example.david.lists.di.data.AppComponent;

public final class MyApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = new InitRelease(this).init();
    }

    public AppComponent getAppComponent() {
        return this.appComponent;
    }
}
package com.example.david.lists.application;

import android.app.Application;

import com.example.david.lists.di.data.AppComponent;

import timber.log.Timber;

final class InitDebug {

    private final Application application;

    InitDebug(Application application) {
        this.application = application;
    }

    public AppComponent init() {
        AppComponent appComponent = new InitRelease(application).init();
        initTimber();
        return appComponent;
    }

    private void initTimber() {
        Timber.plant(new Timber.DebugTree());
    }
}

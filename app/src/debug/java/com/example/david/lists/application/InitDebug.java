package com.example.david.lists.application;

import android.app.Application;

import com.example.david.lists.di.data.AppComponent;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

final class InitDebug {

    private final Application application;

    InitDebug(Application application) {
        this.application = application;
    }

    public AppComponent init() {
        AppComponent appComponent = new InitRelease(application).init();
        initTimber();
        initLeakCanary();
        return appComponent;
    }

    private void initTimber() {
        Timber.plant(new Timber.DebugTree());
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in context process.
            return;
        }
        LeakCanary.install(application);
    }
}

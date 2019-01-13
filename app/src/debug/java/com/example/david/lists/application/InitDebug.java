package com.example.david.lists.application;

import android.app.Application;

import com.example.david.lists.di.data.ModelComponent;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

final class InitDebug {

    private ModelComponent modelComponent;

    InitDebug() {
    }

    public InitDebug init(Application application) {
        modelComponent = new InitRelease().init(application).getModelComponent();
        initTimber();
        initLeakCanary(application);
        return this;
    }

    private void initTimber() {
        Timber.plant(new Timber.DebugTree());
    }

    private void initLeakCanary(Application application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in context process.
            return;
        }
        LeakCanary.install(application);
    }


    ModelComponent getModelComponent() {
        return this.modelComponent;
    }
}

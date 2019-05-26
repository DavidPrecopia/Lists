package com.example.david.lists.application;

import timber.log.Timber;

public final class ListsApplicationImpl extends ListsApplicationBase {
    @Override
    public void onCreate() {
        super.onCreate();
        initTimber();
    }

    private void initTimber() {
        Timber.plant(new Timber.DebugTree());
    }
}
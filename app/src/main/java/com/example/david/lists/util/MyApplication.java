package com.example.david.lists.util;

import android.app.Application;

import com.example.david.lists.BuildConfig;

import androidx.appcompat.app.AppCompatDelegate;
import timber.log.Timber;

public final class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
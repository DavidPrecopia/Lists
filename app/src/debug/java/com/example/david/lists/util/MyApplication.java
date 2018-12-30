package com.example.david.lists.util;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.crashlytics.android.core.CrashlyticsCore;
import com.example.david.lists.R;
import com.squareup.leakcanary.LeakCanary;

import androidx.appcompat.app.AppCompatDelegate;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public final class MyApplication extends Application {

    private static final int PREF_NOT_FOUND = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        checkNetworkConnection();
        setNightMode();
        initFabric();

        initTimber();
        initLeakCanary();
    }

    private void checkNetworkConnection() {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getApplicationContext(), R.string.error_msg_no_network_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void setNightMode() {
        switch (getCurrentMode()) {
            case PREF_NOT_FOUND:
                UtilNightMode.setDay();
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                UtilNightMode.setDay();
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                UtilNightMode.setNight();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private int getCurrentMode() {
        return getSharedPreferences(getString(R.string.night_mode_shared_pref_name), MODE_PRIVATE)
                .getInt(getString(R.string.night_mode_shared_pref_key), PREF_NOT_FOUND);
    }

    private void initFabric() {
        Fabric.with(this, new CrashlyticsCore());
    }


    private void initTimber() {
        Timber.plant(new Timber.DebugTree());
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
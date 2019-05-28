package com.example.david.lists.application;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.example.david.lists.BuildConfig;
import com.example.david.lists.R;
import com.example.david.lists.di.data.AppComponent;
import com.example.david.lists.di.data.DaggerAppComponent;
import com.example.david.lists.util.UtilNightMode;

import io.fabric.sdk.android.Fabric;

abstract class ListsApplicationBase extends Application {

    private AppComponent appComponent;

    private static final int PREF_NOT_FOUND = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        this.appComponent = initAppComponent();
        init();
    }

    private AppComponent initAppComponent() {
        return DaggerAppComponent.builder()
                .application(this)
                .build();
    }


    public AppComponent getAppComponent() {
        return this.appComponent;
    }


    private void init() {
        checkNetworkConnection();
        setNightMode();
        initFabric();
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
        return appComponent.sharedPrefsNightMode()
                .getInt(getString(R.string.night_mode_shared_pref_key), PREF_NOT_FOUND);
    }

    private void initFabric() {
        // Initializes Fabric only for builds that are not of the debug build type.
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);
    }
}

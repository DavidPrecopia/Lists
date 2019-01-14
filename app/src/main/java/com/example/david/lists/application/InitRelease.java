package com.example.david.lists.application;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.crashlytics.android.core.CrashlyticsCore;
import com.example.david.lists.R;
import com.example.david.lists.di.data.AppComponent;
import com.example.david.lists.di.data.DaggerAppComponent;
import com.example.david.lists.util.UtilNightMode;

import androidx.appcompat.app.AppCompatDelegate;
import io.fabric.sdk.android.Fabric;

import static android.content.Context.CONNECTIVITY_SERVICE;

final class InitRelease {

    private final Application application;
    private AppComponent appComponent;

    private static final int PREF_NOT_FOUND = -1;

    InitRelease(Application application) {
        this.application = application;
    }

    AppComponent init() {
        appComponent = initAppComponent();
        checkNetworkConnection();
        setNightMode();
        initFabric();
        return appComponent;
    }

    private AppComponent initAppComponent() {
        return DaggerAppComponent.builder()
                .application(application)
                .build();
    }

    private void checkNetworkConnection() {
        NetworkInfo networkInfo = ((ConnectivityManager) application.getSystemService(CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(application, R.string.error_msg_no_network_connection, Toast.LENGTH_LONG).show();
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
        return appComponent.getSharedPrefsNightMode()
                .getInt(application.getString(R.string.night_mode_shared_pref_key), PREF_NOT_FOUND);
    }

    private void initFabric() {
        Fabric.with(application, new CrashlyticsCore());
    }
}

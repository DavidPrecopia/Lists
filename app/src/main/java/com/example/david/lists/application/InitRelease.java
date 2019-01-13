package com.example.david.lists.application;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.crashlytics.android.core.CrashlyticsCore;
import com.example.david.lists.R;
import com.example.david.lists.di.data.DaggerModelComponent;
import com.example.david.lists.di.data.ModelComponent;
import com.example.david.lists.util.UtilNightMode;

import androidx.appcompat.app.AppCompatDelegate;
import io.fabric.sdk.android.Fabric;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

final class InitRelease {

    private static final int PREF_NOT_FOUND = -1;

    InitRelease() {
    }

    InitRelease init(Application application) {
        checkNetworkConnection(application);
        setNightMode(application);
        initFabric(application);
        return this;
    }

    private void checkNetworkConnection(Application application) {
        NetworkInfo networkInfo = ((ConnectivityManager) application.getSystemService(CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(application, R.string.error_msg_no_network_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void setNightMode(Application application) {
        switch (getCurrentMode(application)) {
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

    private int getCurrentMode(Application application) {
        return application.getSharedPreferences(application.getString(R.string.night_mode_shared_pref_name), MODE_PRIVATE)
                .getInt(application.getString(R.string.night_mode_shared_pref_key), PREF_NOT_FOUND);
    }

    private void initFabric(Application application) {
        Fabric.with(application, new CrashlyticsCore());
    }


    ModelComponent getModelComponent() {
        return DaggerModelComponent.create();
    }
}

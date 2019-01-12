package com.example.david.lists.util;

import android.app.Application;
import android.widget.Toast;

import com.crashlytics.android.core.CrashlyticsCore;
import com.example.david.lists.R;
import com.example.david.lists.di.data.DaggerModelComponent;
import com.example.david.lists.di.data.ModelComponent;

import androidx.appcompat.app.AppCompatDelegate;
import io.fabric.sdk.android.Fabric;

public final class MyApplication extends Application {

    private ModelComponent modelComponent;

    private static final int PREF_NOT_FOUND = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        initModelComponent();
        checkNetworkConnection();
        setNightMode();
        initFabric();
    }

    private void initModelComponent() {
        this.modelComponent = DaggerModelComponent.create();
    }

    private void checkNetworkConnection() {
        if (!UtilNetwork.haveNetworkConnection(getApplicationContext())) {
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


    public ModelComponent getModelComponent() {
        return this.modelComponent;
    }
}
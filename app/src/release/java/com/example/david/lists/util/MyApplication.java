package com.example.david.lists.util;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.david.lists.R;

import androidx.appcompat.app.AppCompatDelegate;

public final class MyApplication extends Application {

    private static final int PREF_NOT_FOUND = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        checkNetworkConnection();
        setNightMode();
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
}
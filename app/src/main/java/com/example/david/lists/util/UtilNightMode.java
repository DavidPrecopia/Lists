package com.example.david.lists.util;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.david.lists.R;
import com.example.david.lists.common.ListsApplication;

public final class UtilNightMode implements IUtilNightModeContract {

    private final Application application;

    public UtilNightMode(Application application) {
        this.application = application;
    }


    @Override
    public void setDay() {
        setMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void setNight() {
        setMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    public boolean isNightModeEnabled() {
        return AppCompatDelegate.MODE_NIGHT_YES ==
                getSharedPrefs().getInt(getKey(), -1);
    }


    private void setMode(int mode) {
        saveChange(mode);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private void saveChange(int mode) {
        SharedPreferences.Editor editor = getSharedPrefs().edit();
        editor.putInt(getKey(), mode);
        editor.apply();
    }


    private SharedPreferences getSharedPrefs() {
        return ((ListsApplication) application).getAppComponent().sharedPrefsNightMode();
    }

    private String getKey() {
        return application.getString(R.string.night_mode_shared_pref_key);
    }
}

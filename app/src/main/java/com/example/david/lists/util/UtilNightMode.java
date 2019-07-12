package com.example.david.lists.util;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.david.lists.R;
import com.example.david.lists.common.ListsApplicationImpl;

public final class UtilNightMode {
    private UtilNightMode() {
    }

    public static void setDay(Application application) {
        setMode(application, AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void setNight(Application application) {
        setMode(application, AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static boolean isNightModeEnabled(Application application) {
        return AppCompatDelegate.MODE_NIGHT_YES ==
                getSharedPrefs(application).getInt(getKey(application), -1);
    }


    private static void setMode(Application application, int mode) {
        saveChange(application, mode);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private static void saveChange(Application application, int mode) {
        SharedPreferences.Editor editor = getSharedPrefs(application).edit();
        editor.putInt(getKey(application), mode);
        editor.apply();
    }


    private static SharedPreferences getSharedPrefs(Application application) {
        return ((ListsApplicationImpl) application).getAppComponent().sharedPrefsNightMode();
    }

    private static String getKey(Application application) {
        return application.getString(R.string.night_mode_shared_pref_key);
    }
}

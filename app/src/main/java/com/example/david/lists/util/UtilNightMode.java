package com.example.david.lists.util;

import androidx.appcompat.app.AppCompatDelegate;

public final class UtilNightMode {
    private UtilNightMode() {
    }

    public static void setDay() {
        setMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void setNight() {
        setMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    private static void setMode(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
    }
}

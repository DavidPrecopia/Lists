package com.example.david.lists.util;

import androidx.appcompat.app.AppCompatDelegate;

public final class UtilNightMode {
    private UtilNightMode() {
    }

    public static void setDay() {
        changeMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void setNight() {
        changeMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    private static void changeMode(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
    }
}

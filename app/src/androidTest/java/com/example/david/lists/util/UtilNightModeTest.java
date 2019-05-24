package com.example.david.lists.util;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UtilNightModeTest {
    @Test
    public void setDayTest() {
        UtilNightMode.setDay();
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, getCurrentNightModeStatus());
    }

    @Test
    public void setNightTest() {
        UtilNightMode.setNight();
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, getCurrentNightModeStatus());
    }

    private int getCurrentNightModeStatus() {
        return AppCompatDelegate.getDefaultNightMode();
    }
}
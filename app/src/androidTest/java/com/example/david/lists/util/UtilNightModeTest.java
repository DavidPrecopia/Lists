package com.example.david.lists.util;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class UtilNightModeTest {

    private UtilNightMode utilNightMode = new UtilNightMode(
            ApplicationProvider.getApplicationContext()
    );


    @Test
    public void setDayTest() {
        utilNightMode.setDay();
        assertNightModeEnabled(false);
    }

    @Test
    public void setNightTest() {
        utilNightMode.setNight();
        assertNightModeEnabled(true);
    }

    private void assertNightModeEnabled(boolean expectation) {
        assertThat(utilNightMode.isNightModeEnabled(), is(expectation));
    }
}
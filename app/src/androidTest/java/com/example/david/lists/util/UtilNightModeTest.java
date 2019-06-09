package com.example.david.lists.util;

import android.app.Application;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class UtilNightModeTest {

    private Application application;

    @Before
    public void init() {
        application = (Application) InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext()
                .getApplicationContext();
    }


    @Test
    public void setDayTest() {
        UtilNightMode.setDay(application);
        assertNightModeEnabled(false);
    }

    @Test
    public void setNightTest() {
        UtilNightMode.setNight(application);
        assertNightModeEnabled(true);
    }

    private void assertNightModeEnabled(boolean expectation) {
        assertThat(UtilNightMode.isNightModeEnabled(application), is(expectation));
    }


    @Test(expected = Exception.class)
    public void setDayTestNullArgument() {
        UtilNightMode.setDay(null);
    }

    @Test(expected = Exception.class)
    public void setNightNullArgument() {
        UtilNightMode.setNight(null);
    }

    @Test(expected = Exception.class)
    public void checkIfNightModeIsEnabledNullArgument() {
        UtilNightMode.isNightModeEnabled(null);
    }
}
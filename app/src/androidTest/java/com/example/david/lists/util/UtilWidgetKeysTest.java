package com.example.david.lists.util;

import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.david.lists.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class UtilWidgetKeysTest {

    private Context context;
    private int maxValue;
    private int minValue;

    @Before
    public void init() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        maxValue = Integer.MAX_VALUE;
        minValue = Integer.MIN_VALUE;
    }


    @Test
    public void sharedPrefsName() {
        String result = UtilWidgetKeys.getSharedPrefName(context);
        assertThat(result, is(context.getString(R.string.widget_shared_prefs_name)));
    }


    @Test
    public void sharedPrefsKeyIdMaxValue() {
        String result = UtilWidgetKeys.getSharedPrefKeyId(context, maxValue);
        assertThat(result, is(context.getString(R.string.widget_key_shared_pref_user_list_id, maxValue)));
    }

    @Test
    public void sharedPrefsKeyIdMinValue() {
        String result = UtilWidgetKeys.getSharedPrefKeyId(context, minValue);
        assertThat(result, is(context.getString(R.string.widget_key_shared_pref_user_list_id, minValue)));
    }


    @Test
    public void sharedPrefsKeyTitleMaxValue() {
        String result = UtilWidgetKeys.getSharedPrefKeyTitle(context, maxValue);
        assertThat(result, is(context.getString(R.string.widget_key_shared_pref_user_list_title, maxValue)));
    }

    @Test
    public void sharedPrefsKeyTitleMinValue() {
        String result = UtilWidgetKeys.getSharedPrefKeyTitle(context, minValue);
        assertThat(result, is(context.getString(R.string.widget_key_shared_pref_user_list_title, minValue)));
    }


    @Test(expected = Exception.class)
    public void sharedPrefsNameNullArgument() {
        UtilWidgetKeys.getSharedPrefName(null);
    }

    @Test(expected = Exception.class)
    public void sharedPrefsKeyIdNullArgument() {
        UtilWidgetKeys.getSharedPrefKeyId(null, 0);
    }

    @Test(expected = Exception.class)
    public void sharedPrefsKeyTitleNullArgument() {
        UtilWidgetKeys.getSharedPrefKeyTitle(null, 0);
    }
}

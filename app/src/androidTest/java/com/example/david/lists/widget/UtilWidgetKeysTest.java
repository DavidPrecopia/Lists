package com.example.david.lists.widget;

import android.content.Context;

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
        String result = UtilWidgetKeys.INSTANCE.getSharedPrefName(context);
        assertThat(result, is(context.getString(R.string.widget_shared_prefs_name)));
    }


    @Test
    public void sharedPrefsKeyIdMaxValue() {
        sharedPrefsKeyId(maxValue);
    }

    @Test
    public void sharedPrefsKeyIdMinValue() {
        sharedPrefsKeyId(minValue);
    }

    private void sharedPrefsKeyId(int value) {
        String result = UtilWidgetKeys.INSTANCE.getSharedPrefKeyId(context, value);
        assertThat(result, is(context.getString(R.string.widget_key_shared_pref_user_list_id, value)));
    }


    @Test
    public void sharedPrefsKeyTitleMaxValue() {
        sharedPrefsKeyTitle(maxValue);
    }

    @Test
    public void sharedPrefsKeyTitleMinValue() {
        sharedPrefsKeyTitle(minValue);
    }

    private void sharedPrefsKeyTitle(int value) {
        String result = UtilWidgetKeys.INSTANCE.getSharedPrefKeyTitle(context, value);
        assertThat(result, is(context.getString(R.string.widget_key_shared_pref_user_list_title, value)));
    }


    @Test(expected = Exception.class)
    public void sharedPrefsNameNullArgument() {
        UtilWidgetKeys.INSTANCE.getSharedPrefName(null);
    }

    @Test(expected = Exception.class)
    public void sharedPrefsKeyIdNullArgument() {
        UtilWidgetKeys.INSTANCE.getSharedPrefKeyId(null, 0);
    }

    @Test(expected = Exception.class)
    public void sharedPrefsKeyTitleNullArgument() {
        UtilWidgetKeys.INSTANCE.getSharedPrefKeyTitle(null, 0);
    }
}
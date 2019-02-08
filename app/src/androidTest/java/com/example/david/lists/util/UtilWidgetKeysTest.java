package com.example.david.lists.util;

import android.content.Context;

import com.example.david.lists.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UtilWidgetKeysTest {

    private Context context = ApplicationProvider.getApplicationContext();
    private int appWidgetId = 123;

    @Test
    public void getSharedPrefNameTest() {
        assertEquals(
                UtilWidgetKeys.getSharedPrefName(context),
                getString(R.string.widget_shared_prefs_name)
        );
    }

    @Test
    public void getSharedPrefKeyIdTest() {
        assertEquals(
                UtilWidgetKeys.getSharedPrefKeyId(context, appWidgetId),
                getStringWithWidgetId(R.string.widget_key_shared_pref_user_list_id)
        );
    }

    @Test
    public void getSharedPrefKeyTitleTest() {
        assertEquals(
                UtilWidgetKeys.getSharedPrefKeyTitle(context, appWidgetId),
                getStringWithWidgetId(R.string.widget_key_shared_pref_user_list_title)
        );
    }

    @Test
    public void getIntentBundleNameTest() {
        assertEquals(
                UtilWidgetKeys.getIntentBundleName(context),
                getString(R.string.widget_intent_bundle_name)
        );
    }

    @Test
    public void getIntentKeyIdTest() {
        assertEquals(
                UtilWidgetKeys.getIntentKeyId(context),
                getString(R.string.widget_key_intent_user_list_id)
        );
    }

    @Test
    public void getIntentKeyTitleTest() {
        assertEquals(
                UtilWidgetKeys.getIntentKeyTitle(context),
                getString(R.string.widget_key_intent_user_list_title)
        );
    }


    private String getString(int resourceId) {
        return context.getString(resourceId);
    }

    private String getStringWithWidgetId(int resourceId) {
        return context.getString(resourceId, appWidgetId);
    }
}
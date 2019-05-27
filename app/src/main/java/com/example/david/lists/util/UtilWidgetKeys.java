package com.example.david.lists.util;

import android.content.Context;

import com.example.david.lists.R;

public class UtilWidgetKeys {
    private UtilWidgetKeys() {
    }

    public static String getSharedPrefName(Context context) {
        return context.getString(R.string.widget_shared_prefs_name);
    }

    public static String getSharedPrefKeyId(Context context, int appWidgetId) {
        return context.getString(R.string.widget_key_shared_pref_user_list_id, appWidgetId);
    }

    public static String getSharedPrefKeyTitle(Context context, int appWidgetId) {
        return context.getString(R.string.widget_key_shared_pref_user_list_title, appWidgetId);
    }
}

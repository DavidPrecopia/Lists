package com.example.david.lists.widget;

import android.content.Context;

import com.example.david.lists.R;

class UtilWidgetKeys {
    private UtilWidgetKeys() {
    }

    static String getSharedPrefsName(Context context) {
        return context.getString(R.string.widget_shared_prefs_name);
    }

    static String getSharedPrefKeyId(Context context, int appWidgetId) {
        return context.getString(R.string.widget_key_shared_pref_user_list_id)
                + appWidgetId;
    }

    static String getSharedPrefKeyTitle(Context context, int appWidgetId) {
        return context.getString(R.string.widget_key_shared_pref_user_list_title)
                + appWidgetId;
    }
}

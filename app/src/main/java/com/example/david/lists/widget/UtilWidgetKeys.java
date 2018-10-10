package com.example.david.lists.widget;

import android.content.Context;

import com.example.david.lists.R;

public class UtilWidgetKeys {
    private UtilWidgetKeys() {
    }

    static String getSharedPrefName(Context context) {
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


    public static String getIntentBundleName(Context context) {
        return context.getString(R.string.widget_intent_bundle_name);
    }

    public static String getIntentKeyId(Context context) {
        return context.getString(R.string.widget_key_intent_user_list_id);
    }

    public static String getIntentKeyTitle(Context context) {
        return context.getString(R.string.widget_key_intent_user_list_title);
    }
}

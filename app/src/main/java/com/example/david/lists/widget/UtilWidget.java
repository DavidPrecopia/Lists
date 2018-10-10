package com.example.david.lists.widget;

import android.content.Context;

import com.example.david.lists.R;

class UtilWidget {
    private UtilWidget() {
    }

    static String getSharedPrefsName(Context context) {
        return context.getString(R.string.widget_key_shared_prefs_name);
    }

    static String getSharedPrefTitleKey(Context context, int appWidgetId) {
        return context.getString(R.string.widget_key_shared_pref_user_list_title)
                + appWidgetId;
    }
}

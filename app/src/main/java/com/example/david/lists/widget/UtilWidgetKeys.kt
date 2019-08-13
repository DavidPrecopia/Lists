package com.example.david.lists.widget

import android.content.Context

import com.example.david.lists.R

object UtilWidgetKeys {
    fun getSharedPrefName(context: Context): String =
            context.getString(R.string.widget_shared_prefs_name)

    fun getSharedPrefKeyId(context: Context, appWidgetId: Int): String =
            context.getString(R.string.widget_key_shared_pref_user_list_id, appWidgetId)

    fun getSharedPrefKeyTitle(context: Context, appWidgetId: Int): String =
            context.getString(R.string.widget_key_shared_pref_user_list_title, appWidgetId)
}

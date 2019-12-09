package com.example.david.lists.widget.common

import com.example.david.lists.R

class UtilWidgetKeys(private val getStringRes: (Int) -> String,
                     private val getStringResArg: (Int, Int) -> String) {

    fun getSharedPrefName() =
            getStringRes(R.string.widget_shared_prefs_name)

    fun getSharedPrefKeyId(appWidgetId: Int) =
            getStringResArg(R.string.widget_key_shared_pref_user_list_id, appWidgetId)

    fun getSharedPrefKeyTitle(appWidgetId: Int) =
            getStringResArg(R.string.widget_key_shared_pref_user_list_title, appWidgetId)
}

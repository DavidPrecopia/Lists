package com.example.david.lists.widget.buildlogic

import android.content.Context
import android.content.SharedPreferences
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.widget.UtilWidgetKeys
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class SharedPrefsModule {
    companion object {
        const val SHARED_PREFS = "widget_shared_prefs"
    }

    @ViewScope
    @Provides
    @Named(SHARED_PREFS)
    fun sharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(UtilWidgetKeys.getSharedPrefName(context), Context.MODE_PRIVATE)
    }
}
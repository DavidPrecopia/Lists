package com.example.david.lists.widget.view.buildlogic

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.RemoteViews
import com.example.david.lists.R
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.itemlist.ItemActivity
import com.example.david.lists.widget.UtilWidgetKeys
import com.example.david.lists.widget.buildlogic.SharedPrefsModule.Companion.SHARED_PREFS
import com.example.david.lists.widget.configview.WidgetConfigView
import com.example.david.lists.widget.view.IWidgetRemoteViewContract.Companion.INTENT_ADAPTER
import com.example.david.lists.widget.view.IWidgetRemoteViewContract.Companion.INTENT_CONFIG
import com.example.david.lists.widget.view.IWidgetRemoteViewContract.Companion.INTENT_TITLE
import com.example.david.lists.widget.view.IWidgetRemoteViewContract.Companion.LIST_ID
import com.example.david.lists.widget.view.IWidgetRemoteViewContract.Companion.LIST_TITLE
import com.example.david.lists.widget.view.MyRemoteViewsService
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class WidgetRemoteViewModule {
    @ViewScope
    @Provides
    fun remoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget)
    }

    @ViewScope
    @Provides
    @Named(INTENT_TITLE)
    fun titleIntent(context: Context,
                    @Named(LIST_ID) listId: String,
                    @Named(LIST_TITLE) listTitle: String): Intent {
        return Intent(context, ItemActivity::class.java).apply {
            putExtra(context.getString(R.string.intent_extra_user_list_id), listId)
            putExtra(context.getString(R.string.intent_extra_user_list_title), listTitle)
        }
    }

    @ViewScope
    @Provides
    @Named(INTENT_CONFIG)
    fun configActivityIntent(context: Context,
                             appWidgetId: Int): Intent {
        return Intent(context, WidgetConfigView::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
    }

    @ViewScope
    @Provides
    @Named(INTENT_ADAPTER)
    fun adapterIntent(context: Context,
                      @Named(LIST_ID) listId: String,
                      appWidgetId: Int): Intent {
        return Intent(context, MyRemoteViewsService::class.java).apply {
            putExtra(context.getString(R.string.intent_extra_user_list_id), listId)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // So the system can distinguish between the different widget instances
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
        }
    }


    @ViewScope
    @Provides
    @Named(LIST_ID)
    fun listId(@Named(SHARED_PREFS) sharedPrefs: SharedPreferences,
               context: Context,
               appWidgetId: Int): String {
        return sharedPrefs.getString(UtilWidgetKeys.getSharedPrefKeyId(context, appWidgetId), "")!!
    }

    @ViewScope
    @Provides
    @Named(LIST_TITLE)
    fun listTitle(@Named(SHARED_PREFS) sharedPrefs: SharedPreferences,
                  context: Context,
                  appWidgetId: Int): String {
        return sharedPrefs.getString(UtilWidgetKeys.getSharedPrefKeyTitle(context, appWidgetId), "")!!
    }
}
package com.example.david.lists.widget.view

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class MyAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
//            val remoteViews = WidgetRemoteView(context, widgetId).updateWidget()
//            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }
}

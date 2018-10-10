package com.example.david.lists.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public final class MyAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new WidgetRemoteView(context, appWidgetId).updateWidget();
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}

package com.example.david.lists.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.david.lists.R;
import com.example.david.lists.ui.view.ListActivity;

import static com.example.david.lists.widget.UtilWidget.getSharedPrefTitleKey;
import static com.example.david.lists.widget.UtilWidget.getSharedPrefsName;

final class UtilWidgetRemoteView {
    UtilWidgetRemoteView() {
    }


    RemoteViews updateWidget(Context context, int appWidgetId) {
        Intent intent = new Intent(context, ListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);

        remoteViews.setTextViewText(R.id.widget_tv_title, getTitle(context, appWidgetId));

        return remoteViews;
    }

    private CharSequence getTitle(Context context, int appWidgetId) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                getSharedPrefsName(context),
                Context.MODE_PRIVATE
        );
        return sharedPrefs.getString(getSharedPrefTitleKey(context, appWidgetId), null);
    }
}

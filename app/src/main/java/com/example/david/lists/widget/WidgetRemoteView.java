package com.example.david.lists.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.david.lists.R;
import com.example.david.lists.ui.view.ListActivity;

import static com.example.david.lists.widget.UtilWidgetKeys.getSharedPrefKeyId;
import static com.example.david.lists.widget.UtilWidgetKeys.getSharedPrefKeyTitle;
import static com.example.david.lists.widget.UtilWidgetKeys.getSharedPrefsName;

final class WidgetRemoteView {

    private final RemoteViews remoteViews;
    private final int appWidgetId;

    private final Context context;

    WidgetRemoteView(Context context, int appWidgetId) {
        this.remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        this.appWidgetId = appWidgetId;
        this.context = context;
    }


    RemoteViews updateWidget() {
        setUpRemoveView();
        return remoteViews;
    }

    private void setUpRemoveView() {
        setTitle();
        setTitlePendingIntent();
        setConfigActivityPendingIntent();
        initRemoteAdapter();
    }


    private void setTitlePendingIntent() {
        Intent intent = new Intent(context, ListActivity.class);
        remoteViews.setOnClickPendingIntent(
                R.id.widget_tv_title,
                getPendingIntent(intent)
        );
    }

    private void setTitle() {
        remoteViews.setTextViewText(R.id.widget_tv_title, getTitle());
    }

    private CharSequence getTitle() {
        return getSharedPrefs().getString(getSharedPrefKeyTitle(context, appWidgetId), null);
    }


    private void setConfigActivityPendingIntent() {
        Intent intent = new Intent(context, WidgetConfigActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        remoteViews.setOnClickPendingIntent(
                R.id.widget_iv_settings,
                getPendingIntent(intent)
        );
    }


    private void initRemoteAdapter() {
        Intent adapterIntent = new Intent(context, MyRemoteViewsService.class);
        setAdapterIntentExtras(adapterIntent);
        remoteViews.setRemoteAdapter(R.id.widget_list_view, adapterIntent);
        remoteViews.setEmptyView(R.id.widget_list_view, R.id.widget_tv_error);
    }

    private void setAdapterIntentExtras(Intent adapterIntent) {
        adapterIntent.putExtra(
                context.getString(R.string.widget_key_intent_user_list_id), getListId()
        );
        adapterIntent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId
        );
        // So the system can distinguish between the different widget instances
        adapterIntent.setData(
                Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME))
        );
    }

    private int getListId() {
        return getSharedPrefs().getInt(getSharedPrefKeyId(context, appWidgetId), -1);
    }


    private PendingIntent getPendingIntent(Intent intent) {
        return PendingIntent.getActivity(
                context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private SharedPreferences getSharedPrefs() {
        return context.getSharedPreferences(
                getSharedPrefsName(context),
                Context.MODE_PRIVATE
        );
    }
}

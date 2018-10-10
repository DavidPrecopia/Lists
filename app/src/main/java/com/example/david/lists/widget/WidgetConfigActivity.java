package com.example.david.lists.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.david.lists.R;
import com.example.david.lists.databinding.ActivityWidgetConfigBinding;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import static com.example.david.lists.widget.UtilWidget.getSharedPrefTitleKey;
import static com.example.david.lists.widget.UtilWidget.getSharedPrefsName;

public class WidgetConfigActivity extends AppCompatActivity {

    private ActivityWidgetConfigBinding binding;

    private static final int INVALID_WIDGET_ID = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int widgetId = INVALID_WIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // In case the user cancels
        resultsIntent(RESULT_CANCELED);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_widget_config);
        init();
    }

    private void init() {
        getWidgetId();

        binding.buttonSaveTitle.setOnClickListener(view -> saveEnteredTitle());
    }

    private void getWidgetId() {
        widgetId = Objects.requireNonNull(getIntent().getExtras())
                .getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_WIDGET_ID);
        if (widgetId == INVALID_WIDGET_ID) {
            finish();
        }
    }


    private void saveEnteredTitle() {
        SharedPreferences.Editor editor = getSharedPreferences(
                getSharedPrefsName(getApplicationContext()), MODE_PRIVATE
        ).edit();

        editor.putString(
                getSharedPrefTitleKey(getApplicationContext(), widgetId),
                binding.etEnterTitle.getText().toString()
        );
        editor.apply();

        updateWidget();

        resultsIntent(RESULT_OK);

        finish();
    }


    private void updateWidget() {
        RemoteViews remoteView = new UtilWidgetRemoteView().updateWidget(getApplicationContext(), widgetId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(widgetId, remoteView);
    }


    private void resultsIntent(int resultCode) {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(resultCode, resultValue);
    }
}

package com.example.david.lists.widget.configactivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.databinding.ActivityWidgetConfigBinding;
import com.example.david.lists.util.UtilRecyclerView;
import com.example.david.lists.util.UtilViewModelFactory;
import com.example.david.lists.widget.WidgetRemoteView;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefKeyId;
import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefKeyTitle;
import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefName;

public class WidgetConfigActivity extends AppCompatActivity {

    private IWidgetConfigViewModelContract viewModel;
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
        initViewModel();
        initRecyclerView();
        initToolbar();
        observeViewModel();
    }


    private void getWidgetId() {
        widgetId = Objects.requireNonNull(getIntent().getExtras())
                .getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_WIDGET_ID);
        if (widgetId == INVALID_WIDGET_ID) {
            finish();
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        UtilRecyclerView.initLayoutManager(recyclerView);
        recyclerView.setAdapter(viewModel.getAdapter());
    }

    private void initToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_widget_config_activity);
    }


    private void initViewModel() {
        UtilViewModelFactory factory = new UtilViewModelFactory(getApplication(), null);
        viewModel = ViewModelProviders.of(this, factory).get(WidgetConfigViewModel.class);
    }

    private void observeViewModel() {
        observeEventDisplayingLoading();
        observeEventDisplayError();
        observeEventGroupSelected();
    }

    private void observeEventDisplayingLoading() {
        viewModel.getEventDisplayLoading().observe(this, display -> {
            if (display) {
                showLoading();
            } else {
                hideLoading();
            }
        });
    }

    private void observeEventDisplayError() {
        viewModel.getEventDisplayError().observe(this, this::showError);
    }

    private void observeEventGroupSelected() {
        viewModel.getEventSelectGroup().observe(this, this::applySelection);
    }


    private void applySelection(Group group) {
        saveDetails(group.getId(), group.getTitle());
        resultsIntent(RESULT_OK);
        updateWidget();
        finish();
    }

    private void saveDetails(String id, String title) {
        SharedPreferences.Editor editor = getSharedPreferences(
                getSharedPrefName(getApplicationContext()), MODE_PRIVATE
        ).edit();
        editor.putString(getSharedPrefKeyId(getApplicationContext(), widgetId), id);
        editor.putString(getSharedPrefKeyTitle(getApplicationContext(), widgetId), title);
        editor.apply();
    }

    private void updateWidget() {
        RemoteViews remoteView = new WidgetRemoteView(getApplicationContext(), widgetId).updateWidget();
        AppWidgetManager.getInstance(this).updateAppWidget(widgetId, remoteView);
    }


    private void resultsIntent(int resultCode) {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(resultCode, resultValue);
    }


    private void showLoading() {
        hideError();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        hideError();
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        TextView tvError = binding.tvError;
        binding.recyclerView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        binding.tvError.setVisibility(View.GONE);
    }
}
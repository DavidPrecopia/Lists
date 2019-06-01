package com.example.david.lists.widget.configactivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.databinding.ActivityWidgetConfigBinding;
import com.example.david.lists.widget.di.DaggerWidgetConfigComponent;

import javax.inject.Inject;
import javax.inject.Provider;

public class WidgetConfigActivity extends AppCompatActivity {

    private ActivityWidgetConfigBinding binding;

    @Inject
    IWidgetConfigViewModel viewModel;

    @Inject
    IWidgetConfigAdapter adapter;
    @Inject
    Provider<LinearLayoutManager> layoutManger;
    @Inject
    RecyclerView.ItemDecoration dividerItemDecorator;

    private static final int INVALID_WIDGET_ID = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int widgetId = INVALID_WIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inject();
        super.onCreate(savedInstanceState);
        // In case the user cancels
        resultsIntent(RESULT_CANCELED);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_widget_config);
        init();
    }

    private void inject() {
        DaggerWidgetConfigComponent.builder()
                .application(getApplication())
                .activity(this)
                .widgetId(getWidgetId())
                .build()
                .inject(this);
    }

    private void init() {
        getWidgetId();
        initRecyclerView();
        initToolbar();
        observeViewModel();
    }


    private int getWidgetId() {
        widgetId = getIntent().getExtras()
                .getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_WIDGET_ID);
        if (widgetId == INVALID_WIDGET_ID) {
            finish();
        }
        return widgetId;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManger.get());
        recyclerView.addItemDecoration(dividerItemDecorator);
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);
    }

    private void initToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_widget_config_activity);
    }

    private void observeViewModel() {
        observeUserLists();
        observeEventDisplayingLoading();
        observeEventDisplayError();
        observeEventSuccessful();
    }

    private void observeUserLists() {
        viewModel.getUserLists().observe(this, userLists -> adapter.submitList(userLists));
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
        viewModel.getEventDisplayError().observe(this, display -> {
            if (display) {
                showError(viewModel.getErrorMessage().getValue());
            } else {
                hideError();
            }
        });
    }


    private void observeEventSuccessful() {
        viewModel.getEventSuccessful().observe(this, aVoid -> successful());
    }

    private void successful() {
        resultsIntent(RESULT_OK);
        finish();
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
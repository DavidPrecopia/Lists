package com.example.david.lists.widget.configactivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.david.lists.R;
import com.example.david.lists.databinding.ActivityWidgetConfigBinding;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        initLayoutManager(recyclerView);
        recyclerView.setAdapter(viewModel.getAdapter());
    }

    private void initLayoutManager(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerDecorator(recyclerView, layoutManager));
    }

    private DividerItemDecoration getDividerDecorator(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
    }

    private void initToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_widget_config_activity);
    }


    private void initViewModel() {
        WidgetConfigViewModelFactory factory = new WidgetConfigViewModelFactory(getApplication(), this.widgetId);
        viewModel = ViewModelProviders.of(this, factory).get(WidgetConfigViewModel.class);
    }

    private void observeViewModel() {
        observeEventDisplayingLoading();
        observeEventDisplayError();
        observeEventSuccessful();
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
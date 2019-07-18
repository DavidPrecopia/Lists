package com.example.david.lists.widget.configview;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.databinding.WidgetConfigViewBinding;
import com.example.david.lists.widget.configview.buildlogic.DaggerWidgetConfigViewComponent;
import com.example.david.lists.widget.view.WidgetRemoteView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

public class WidgetConfigView extends AppCompatActivity
        implements IWidgetConfigContract.View {

    private WidgetConfigViewBinding binding;

    @Inject
    IWidgetConfigContract.Logic logic;

    @Inject
    IWidgetConfigContract.Adapter adapter;
    @Inject
    Provider<LinearLayoutManager> layoutManger;
    @Inject
    RecyclerView.ItemDecoration dividerItemDecorator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inject();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.widget_config_view);
        initView();

        logic.onStart(getWidgetId());
    }

    private void inject() {
        DaggerWidgetConfigViewComponent.builder()
                .application(getApplication())
                .view(this)
                .widgetId(getWidgetId())
                .build()
                .inject(this);
    }

    private int getWidgetId() {
        return getIntent().getExtras().getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );
    }


    private void initView() {
        getWidgetId();
        initRecyclerView();
        initToolbar();
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


    @Override
    public void setResults(int widgetId, int resultCode) {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(resultCode, resultValue);
    }

    @Override
    public void finishView(int widgetId) {
        updateWidget(widgetId);
        super.finish();
    }

    private void updateWidget(int widgetId) {
        RemoteViews remoteView = new WidgetRemoteView(getApplication(), widgetId).updateWidget();
        AppWidgetManager.getInstance(getApplication()).updateAppWidget(widgetId, remoteView);
    }

    @Override
    public void finishViewInvalidId() {
        super.finish();
    }


    @Override
    public void setData(List<UserList> list) {
        adapter.setData(list);
    }


    @Override
    public void setStateDisplayList() {
        binding.progressBar.setVisibility(View.GONE);
        binding.tvError.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setStateLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void setStateError(String message) {
        binding.recyclerView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);

        TextView tvError = binding.tvError;
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        logic.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        logic.onDestroy();
        super.onBackPressed();
    }
}
package com.example.david.lists.widget.di;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.widget.configactivity.IWidgetConfigAdapter;
import com.example.david.lists.widget.configactivity.IWidgetConfigViewModel;
import com.example.david.lists.widget.configactivity.WidgetConfigAdapterImpl;
import com.example.david.lists.widget.configactivity.WidgetConfigViewModelFactory;
import com.example.david.lists.widget.configactivity.WidgetConfigViewModelImpl;

import dagger.Module;
import dagger.Provides;

@Module
final class WidgetConfigModule {
    @WidgetConfigScope
    @Provides
    IWidgetConfigViewModel viewModel(AppCompatActivity activity, ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(activity, factory).get(WidgetConfigViewModelImpl.class);
    }

    @WidgetConfigScope
    @Provides
    ViewModelProvider.Factory factory(Application application, IRepository repository, int widgetId) {
        return new WidgetConfigViewModelFactory(application, repository, widgetId);
    }

    @WidgetConfigScope
    @Provides
    IWidgetConfigAdapter adapter(IWidgetConfigViewModel viewModel) {
        return new WidgetConfigAdapterImpl(viewModel);
    }
}

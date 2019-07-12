package com.example.david.lists.widget.configview.buildlogic;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.widget.configview.IWidgetConfigContract;
import com.example.david.lists.widget.configview.WidgetConfigAdapter;
import com.example.david.lists.widget.configview.WidgetConfigViewModel;
import com.example.david.lists.widget.configview.WidgetConfigViewModelFactory;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class WidgetConfigViewModule {
    @ViewScope
    @Provides
    IWidgetConfigContract.ViewModel viewModel(AppCompatActivity activity, ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(activity, factory).get(WidgetConfigViewModel.class);
    }

    @ViewScope
    @Provides
    ViewModelProvider.Factory factory(Application application, IRepositoryContract.Repository repository, CompositeDisposable disposable, int widgetId) {
        return new WidgetConfigViewModelFactory(application, repository, disposable, widgetId);
    }

    @ViewScope
    @Provides
    IWidgetConfigContract.Adapter adapter(IWidgetConfigContract.ViewModel viewModel) {
        return new WidgetConfigAdapter(viewModel);
    }
}

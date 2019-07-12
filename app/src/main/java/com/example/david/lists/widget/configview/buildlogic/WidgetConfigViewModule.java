package com.example.david.lists.widget.configview.buildlogic;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.widget.configview.IWidgetConfigAdapter;
import com.example.david.lists.widget.configview.IWidgetConfigViewModel;
import com.example.david.lists.widget.configview.WidgetConfigAdapterImpl;
import com.example.david.lists.widget.configview.WidgetConfigViewModelFactory;
import com.example.david.lists.widget.configview.WidgetConfigViewModelImpl;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class WidgetConfigViewModule {
    @ViewScope
    @Provides
    IWidgetConfigViewModel viewModel(AppCompatActivity activity, ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(activity, factory).get(WidgetConfigViewModelImpl.class);
    }

    @ViewScope
    @Provides
    ViewModelProvider.Factory factory(Application application, IRepository repository, CompositeDisposable disposable, int widgetId) {
        return new WidgetConfigViewModelFactory(application, repository, disposable, widgetId);
    }

    @ViewScope
    @Provides
    IWidgetConfigAdapter adapter(IWidgetConfigViewModel viewModel) {
        return new WidgetConfigAdapterImpl(viewModel);
    }
}

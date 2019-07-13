package com.example.david.lists.widget.configview.buildlogic;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.widget.UtilWidgetKeys;
import com.example.david.lists.widget.configview.IWidgetConfigContract;
import com.example.david.lists.widget.configview.WidgetConfigAdapter;
import com.example.david.lists.widget.configview.WidgetConfigLogic;
import com.example.david.lists.widget.configview.WidgetConfigViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

import static android.content.Context.MODE_PRIVATE;

@Module
final class WidgetConfigViewModule {
    @ViewScope
    @Provides
    IWidgetConfigContract.Logic logic(IWidgetConfigContract.View view,
                                      IWidgetConfigContract.ViewModel viewModel,
                                      IRepositoryContract.Repository repository,
                                      ISchedulerProviderContract schedulerProvider,
                                      CompositeDisposable disposable,
                                      @Named(WidgetConfigViewComponent.SHARED_PREFS) SharedPreferences sharedPrefs,
                                      int widgetId) {
        return new WidgetConfigLogic(view, viewModel, repository, schedulerProvider, disposable, sharedPrefs, widgetId);
    }

    @ViewScope
    @Provides
    IWidgetConfigContract.ViewModel viewModel(Application application) {
        return new WidgetConfigViewModel(application);
    }

    @ViewScope
    @Provides
    @Named(WidgetConfigViewComponent.SHARED_PREFS)
    SharedPreferences sharedPrefs(Application application) {
        return application.getSharedPreferences(UtilWidgetKeys.getSharedPrefName(application), MODE_PRIVATE);
    }

    @ViewScope
    @Provides
    IWidgetConfigContract.Adapter adapter(IWidgetConfigContract.Logic logic) {
        return new WidgetConfigAdapter(logic);
    }
}

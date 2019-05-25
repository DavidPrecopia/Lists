package com.example.david.lists.widget.di;

import android.app.Application;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.ui.common.TouchHelperCallback;
import com.example.david.lists.widget.configactivity.IWidgetConfigViewModel;
import com.example.david.lists.widget.configactivity.WidgetConfigActivity;
import com.example.david.lists.widget.configactivity.WidgetConfigAdapter;
import com.example.david.lists.widget.configactivity.WidgetConfigViewModelFactory;
import com.example.david.lists.widget.configactivity.WidgetConfigViewModelImpl;

import dagger.Module;
import dagger.Provides;

@Module
final class WidgetConfigModule {
    @WidgetConfigScope
    @Provides
    IWidgetConfigViewModel viewModel(WidgetConfigActivity activity, WidgetConfigViewModelFactory factory) {
        return ViewModelProviders.of(activity, factory).get(WidgetConfigViewModelImpl.class);
    }

    @WidgetConfigScope
    @Provides
    WidgetConfigViewModelFactory factory(Application application, int widgetId, IRepository repository) {
        return new WidgetConfigViewModelFactory(application, widgetId, repository);
    }

    @WidgetConfigScope
    @Provides
    IRepository repository(Application application) {
        return ((MyApplication) application).getAppComponent().repository();
    }

    @WidgetConfigScope
    @Provides
    WidgetConfigAdapter adapter(IWidgetConfigViewModel viewModel) {
        return new WidgetConfigAdapter(viewModel);
    }

    @WidgetConfigScope
    @Provides
    RecyclerView.ItemDecoration itemDecoration(Application application, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(application.getApplicationContext(), layoutManager.getOrientation());
    }

    @WidgetConfigScope
    @Provides
    LinearLayoutManager layoutManager(Application application) {
        return new LinearLayoutManager(application.getApplicationContext());
    }

    @WidgetConfigScope
    @Provides
    ItemTouchHelper itemTouchHelper(TouchHelperCallback.MovementCallback movementCallback) {
        return new ItemTouchHelper(new TouchHelperCallback(movementCallback));
    }
}

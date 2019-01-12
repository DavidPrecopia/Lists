package com.example.david.lists.widget.configactivity;

import android.app.Application;

import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.util.MyApplication;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

final class WidgetConfigViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    @NonNull
    private final Application application;
    private final int widgetId;
    private final IModelContract model;

    WidgetConfigViewModelFactory(@NonNull Application application, int widgetId) {
        super(application);
        this.application = application;
        this.widgetId = widgetId;
        this.model = ((MyApplication) application.getApplicationContext()).getModelComponent().getModel();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WidgetConfigViewModel.class)) {
            //noinspection unchecked
            return (T) new WidgetConfigViewModel(application, model, widgetId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

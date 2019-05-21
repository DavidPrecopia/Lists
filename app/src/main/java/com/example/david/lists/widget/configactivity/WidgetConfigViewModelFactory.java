package com.example.david.lists.widget.configactivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.david.lists.data.repository.IRepository;

public final class WidgetConfigViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    @NonNull
    private final Application application;
    private final int widgetId;
    private final IRepository repository;

    public WidgetConfigViewModelFactory(@NonNull Application application, int widgetId, IRepository repository) {
        super(application);
        this.application = application;
        this.widgetId = widgetId;
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WidgetConfigViewModelImpl.class)) {
            //noinspection unchecked
            return (T) new WidgetConfigViewModelImpl(application, repository, widgetId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

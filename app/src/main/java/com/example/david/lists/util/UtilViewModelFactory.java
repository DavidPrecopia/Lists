package com.example.david.lists.util;

import android.app.Application;

import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.data.model.Model;
import com.example.david.lists.ui.viewmodels.GroupViewModel;
import com.example.david.lists.ui.viewmodels.ItemViewModel;
import com.example.david.lists.widget.configactivity.WidgetConfigViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public final class UtilViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    @Nullable
    private final String groupId;
    private final IModelContract model;

    public UtilViewModelFactory(@NonNull Application application, @Nullable String groupId) {
        super(application);
        this.application = application;
        this.groupId = groupId;
        this.model = Model.getInstance();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GroupViewModel.class)) {
            //noinspection unchecked
            return (T) new GroupViewModel(application, model);
        } else if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application, model, groupId);
        } else if (modelClass.isAssignableFrom(WidgetConfigViewModel.class)) {
            //noinspection unchecked
//            return (T) new WidgetConfigViewModel(application, model);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
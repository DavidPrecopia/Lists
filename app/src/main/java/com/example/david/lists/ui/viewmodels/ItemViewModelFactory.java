package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.data.model.Model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

final class ItemViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final String groupId;
    private final IModelContract model;

    ItemViewModelFactory(@NonNull Application application, String groupId) {
        super(application);
        this.application = application;
        this.groupId = groupId;
        this.model = Model.getInstance();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application, model, groupId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
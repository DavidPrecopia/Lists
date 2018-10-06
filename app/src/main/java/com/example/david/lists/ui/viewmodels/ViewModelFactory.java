package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.model.IModelContract;
import com.example.david.lists.model.Model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

final class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final IModelContract model;

    ViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
        this.model = Model.getInstance(application);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            //noinspection unchecked
            return (T) new UserListViewModel(application, model);
        } else if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application, model);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
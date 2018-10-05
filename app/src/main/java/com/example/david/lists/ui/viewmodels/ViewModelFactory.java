package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

final class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;

    ViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            //noinspection unchecked
            return (T) new UserListViewModel(application);
        } else if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
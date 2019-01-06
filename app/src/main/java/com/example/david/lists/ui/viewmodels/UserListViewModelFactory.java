package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.data.model.Model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public final class UserListViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final IModelContract model;

    public UserListViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
        this.model = Model.getInstance();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            //noinspection unchecked
            return (T) new UserListViewModel(application, model);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
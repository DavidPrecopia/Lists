package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.data.model.Model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

final class GroupViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final IModelContract model;

    GroupViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
        this.model = Model.getInstance();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GroupViewModel.class)) {
            //noinspection unchecked
            return (T) new GroupViewModel(application, model);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
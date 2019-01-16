package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.data.model.IModelContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public final class ItemViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final String userListId;
    private final IModelContract model;

    public ItemViewModelFactory(@NonNull Application application, IModelContract model, String userListId) {
        super(application);
        this.application = application;
        this.model = model;
        this.userListId = userListId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application, model, userListId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
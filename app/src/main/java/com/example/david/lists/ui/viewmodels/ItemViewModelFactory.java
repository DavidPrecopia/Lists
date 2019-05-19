package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.david.lists.data.repository.IRepository;

public final class ItemViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final String userListId;
    private final IRepository repository;

    public ItemViewModelFactory(@NonNull Application application, IRepository repository, String userListId) {
        super(application);
        this.application = application;
        this.repository = repository;
        this.userListId = userListId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModelImpl.class)) {
            //noinspection unchecked
            return (T) new ItemViewModelImpl(application, repository, userListId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
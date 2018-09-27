package com.example.david.lists.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

final class DetailViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    @NonNull
    private final Application application;
    private final int listId;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    DetailViewModelFactory(@NonNull Application application, int listId) {
        super(application);
        this.application = application;
        this.listId = listId;
    }

    @NonNull
    @Override
    public DetailViewModel create(@NonNull Class modelClass) {
        return new DetailViewModel(application, listId);
    }
}

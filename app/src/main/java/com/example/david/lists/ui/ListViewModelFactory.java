package com.example.david.lists.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

final class ListViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    @NonNull
    private final Application application;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    ListViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @NonNull
    @Override
    public ListViewModel create(@NonNull Class modelClass) {
        return new ListViewModel(application);
    }
}

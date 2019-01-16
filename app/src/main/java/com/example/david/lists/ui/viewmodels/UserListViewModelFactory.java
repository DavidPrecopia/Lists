package com.example.david.lists.ui.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.david.lists.data.model.IModelContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public final class UserListViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final IModelContract model;
    private final SharedPreferences sharedPrefsNightMode;

    public UserListViewModelFactory(Application application, IModelContract model, SharedPreferences sharedPrefsNightMode) {
        super(application);
        this.application = application;
        this.model = model;
        this.sharedPrefsNightMode = sharedPrefsNightMode;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            //noinspection unchecked
            return (T) new UserListViewModel(application, model, sharedPrefsNightMode);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
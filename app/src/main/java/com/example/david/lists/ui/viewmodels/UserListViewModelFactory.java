package com.example.david.lists.ui.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.david.lists.data.repository.IRepository;

public final class UserListViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final IRepository repository;
    private final SharedPreferences sharedPrefsNightMode;

    public UserListViewModelFactory(Application application, IRepository repository, SharedPreferences sharedPrefsNightMode) {
        super(application);
        this.application = application;
        this.repository = repository;
        this.sharedPrefsNightMode = sharedPrefsNightMode;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModelImpl.class)) {
            //noinspection unchecked
            return (T) new UserListViewModelImpl(application, repository, sharedPrefsNightMode);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
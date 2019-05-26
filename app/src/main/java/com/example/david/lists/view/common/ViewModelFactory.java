package com.example.david.lists.view.common;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.data.repository.IUserRepository;
import com.example.david.lists.view.itemlist.ItemViewModelImpl;
import com.example.david.lists.view.userlistlist.UserListViewModelImpl;

public final class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;

    private final IRepository repository;
    private IUserRepository userRepository;

    private SharedPreferences sharedPrefsNightMode;

    private String userListId;

    private ViewModelFactory(@NonNull Application application, IRepository repository) {
        super(application);
        this.application = application;
        this.repository = repository;
    }

    /**
     * Used to create {@link com.example.david.lists.view.userlistlist.UserListViewModelImpl}.
     */
    public ViewModelFactory(Application application, IRepository repository, IUserRepository userRepository, SharedPreferences sharedPrefsNightMode) {
        this(application, repository);
        this.userRepository = userRepository;
        this.sharedPrefsNightMode = sharedPrefsNightMode;
    }

    /**
     * Used to create {@link com.example.david.lists.view.itemlist.ItemViewModelImpl}.
     */
    public ViewModelFactory(@NonNull Application application, IRepository repository, String userListId) {
        this(application, repository);
        this.userListId = userListId;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModelImpl.class)) {
            //noinspection unchecked
            return (T) new UserListViewModelImpl(application, repository, userRepository, sharedPrefsNightMode);
        } else if (modelClass.isAssignableFrom(ItemViewModelImpl.class)) {
            //noinspection unchecked
            return (T) new ItemViewModelImpl(application, repository, userListId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

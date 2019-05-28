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

import io.reactivex.disposables.CompositeDisposable;

public final class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;

    private final IRepository repository;
    private IUserRepository userRepository;

    private final CompositeDisposable disposable;

    private SharedPreferences sharedPrefsNightMode;

    private String userListId;

    private ViewModelFactory(@NonNull Application application, IRepository repository, CompositeDisposable disposable) {
        super(application);
        this.application = application;
        this.repository = repository;
        this.disposable = disposable;
    }

    /**
     * Used to create {@link com.example.david.lists.view.userlistlist.UserListViewModelImpl}.
     */
    public ViewModelFactory(Application application, IRepository repository, IUserRepository userRepository, CompositeDisposable disposable, SharedPreferences sharedPrefsNightMode) {
        this(application, repository, disposable);
        this.userRepository = userRepository;
        this.sharedPrefsNightMode = sharedPrefsNightMode;
    }

    /**
     * Used to create {@link com.example.david.lists.view.itemlist.ItemViewModelImpl}.
     */
    public ViewModelFactory(@NonNull Application application, IRepository repository, CompositeDisposable disposable, String userListId) {
        this(application, repository, disposable);
        this.userListId = userListId;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModelImpl.class)) {
            //noinspection unchecked
            return (T) new UserListViewModelImpl(application, repository, userRepository, disposable, sharedPrefsNightMode);
        } else if (modelClass.isAssignableFrom(ItemViewModelImpl.class)) {
            //noinspection unchecked
            return (T) new ItemViewModelImpl(application, repository, disposable, userListId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

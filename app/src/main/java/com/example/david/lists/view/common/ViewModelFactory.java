package com.example.david.lists.view.common;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.itemlist.ItemViewModel;
import com.example.david.lists.view.userlistlist.UserListViewModel;

import io.reactivex.disposables.CompositeDisposable;

public final class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;

    private final IRepositoryContract.Repository repository;
    private IRepositoryContract.UserRepository userRepository;

    private final CompositeDisposable disposable;

    private SharedPreferences sharedPrefsNightMode;

    private String userListId;

    private ViewModelFactory(@NonNull Application application, IRepositoryContract.Repository repository, CompositeDisposable disposable) {
        super(application);
        this.application = application;
        this.repository = repository;
        this.disposable = disposable;
    }

    /**
     * Used to create {@link UserListViewModel}.
     */
    public ViewModelFactory(Application application, IRepositoryContract.Repository repository, IRepositoryContract.UserRepository userRepository, CompositeDisposable disposable, SharedPreferences sharedPrefsNightMode) {
        this(application, repository, disposable);
        this.userRepository = userRepository;
        this.sharedPrefsNightMode = sharedPrefsNightMode;
    }

    /**
     * Used to create {@link ItemViewModel}.
     */
    public ViewModelFactory(@NonNull Application application, IRepositoryContract.Repository repository, CompositeDisposable disposable, String userListId) {
        this(application, repository, disposable);
        this.userListId = userListId;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            //noinspection unchecked
            return (T) new UserListViewModel(application, repository, userRepository, disposable, sharedPrefsNightMode);
        } else if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application, repository, disposable, userListId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

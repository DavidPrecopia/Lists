package com.example.david.lists.view.common;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.itemlist.ItemViewModel;

import io.reactivex.disposables.CompositeDisposable;

public final class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;

    private final IRepositoryContract.Repository repository;
    private final CompositeDisposable disposable;

    private String userListId;

    private ViewModelFactory(@NonNull Application application, IRepositoryContract.Repository repository, CompositeDisposable disposable) {
        super(application);
        this.application = application;
        this.repository = repository;
        this.disposable = disposable;
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
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application, repository, disposable, userListId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

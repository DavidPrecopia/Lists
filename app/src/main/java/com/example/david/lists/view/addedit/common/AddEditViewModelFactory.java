package com.example.david.lists.view.addedit.common;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.view.addedit.item.AddEditItemViewModel;
import com.example.david.lists.view.addedit.userlist.AddEditUserListViewModel;

import io.reactivex.disposables.CompositeDisposable;

public final class AddEditViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;

    private final IRepository repository;
    private final CompositeDisposable disposable;

    private final String id;
    private final String title;
    private String userListId;

    public AddEditViewModelFactory(Application application,
                                   IRepository repository,
                                   CompositeDisposable disposable,
                                   String id,
                                   String title) {
        super(application);
        this.application = application;
        this.repository = repository;
        this.disposable = disposable;
        this.id = id;
        this.title = title;
        this.userListId = "";
    }

    public AddEditViewModelFactory(Application application,
                                   IRepository repository,
                                   CompositeDisposable disposable,
                                   String id,
                                   String title,
                                   String userListId) {
        this(application, repository, disposable, id, title);
        this.userListId = userListId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddEditUserListViewModel.class)) {
            //noinspection unchecked
            return (T) new AddEditUserListViewModel(application, repository, disposable, id, title);
        } else if (modelClass.isAssignableFrom(AddEditItemViewModel.class)) {
            //noinspection unchecked
            return (T) new AddEditItemViewModel(application, repository, disposable, id, title, userListId);
        } else {
            throw new IllegalArgumentException();
        }
    }
}

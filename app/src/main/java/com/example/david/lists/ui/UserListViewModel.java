package com.example.david.lists.ui;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.model.IModelContract;
import com.example.david.lists.model.Model;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

final class UserListViewModel {

    private final MutableLiveData<List<UserList>> userLists;
    private final MutableLiveData<String> error;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final Application application;

    UserListViewModel(@NonNull Application application) {
        userLists = new MutableLiveData<>();
        error = new MutableLiveData<>();
        model = Model.getInstance(application);
        disposable = new CompositeDisposable();
        this.application = application;

        getAllUserLists();
    }


    private void getAllUserLists() {
        disposable.add(model.getAllLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(userListsSubscriber())
        );
    }

    private DisposableSubscriber<List<UserList>> userListsSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> userLists) {
                Timber.d("onNext");
                UserListViewModel.this.userLists.setValue(userLists);
            }

            @Override
            public void onError(Throwable t) {
                UserListViewModel.this.error.setValue(application.getString(R.string.error_msg_generic));
            }

            @Override
            public void onComplete() {

            }
        };
    }


    void add(String title, int position) {
        Completable.fromAction(() -> model.addList(new UserList(title, position)))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    void delete(int listId) {
        Completable.fromAction(() -> model.deleteList(listId))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    void changeTitle(int listId, String newTitle) {
        Completable.fromAction(() -> model.changeListTitle(listId, newTitle))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    LiveData<List<UserList>> getUserLists() {
        return userLists;
    }

    LiveData<String> getError() {
        return error;
    }
}
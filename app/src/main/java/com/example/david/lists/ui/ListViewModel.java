package com.example.david.lists.ui;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.model.IModelContract;
import com.example.david.lists.model.Model;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

final class ListViewModel extends AndroidViewModel {

    private final MutableLiveData<List<UserList>> userLists;
    private final MutableLiveData<String> error;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    ListViewModel(@NonNull Application application) {
        super(application);
        userLists = new MutableLiveData<>();
        error = new MutableLiveData<>();
        model = Model.getInstance(application);
        disposable = new CompositeDisposable();

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
                Timber.i("onNext");
                ListViewModel.this.userLists.setValue(userLists);
            }

            @Override
            public void onError(Throwable t) {
                Timber.e(t);
                ListViewModel.this.error.setValue(getApplication().getString(R.string.error_msg_generic));
            }

            @Override
            public void onComplete() {
                Timber.i("onComplete");
            }
        };
    }


    void add(String name, int position) {
        Completable.fromAction(() -> model.addList(new UserList(name, position)))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    void delete(int listId) {
        Completable.fromAction(() -> model.deleteList(listId))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    LiveData<List<UserList>> getUserLists() {
        return userLists;
    }

    LiveData<String> getError() {
        return error;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
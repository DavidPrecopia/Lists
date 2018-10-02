package com.example.david.lists.ui.list;

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

    private UserList temporaryUserList;

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
                Timber.d("onNext");
                ListViewModel.this.userLists.setValue(userLists);
            }

            @Override
            public void onError(Throwable t) {
                ListViewModel.this.error.setValue(getApplication().getString(R.string.error_msg_generic));
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


    void prepareToDelete(UserList userList) {
        this.temporaryUserList = userList;
    }

    void permanentlyDelete() {
        checkIfValidTempValue();
        Completable.fromAction(() -> model.deleteList(temporaryUserList.getId()))
                .subscribeOn(Schedulers.io())
                .subscribe();
        temporaryUserList = null;
    }

    List<UserList> undoDeletion() {
        checkIfValidTempValue();
        return userLists.getValue();
    }

    private void checkIfValidTempValue() {
        if (temporaryUserList == null) {
            throw new IllegalStateException(
                    getApplication().getString(R.string.error_invalid_deletion_undo)
            );
        }
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


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
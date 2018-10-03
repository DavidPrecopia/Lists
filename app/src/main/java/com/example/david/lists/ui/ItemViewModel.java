package com.example.david.lists.ui;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.datamodel.Item;
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

final class ItemViewModel {

    private int listId;

    private final MutableLiveData<List<Item>> itemList;
    private final MutableLiveData<String> error;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final Application application;

    ItemViewModel(@NonNull Application application) {
        itemList = new MutableLiveData<>();
        error = new MutableLiveData<>();
        model = Model.getInstance(application);
        disposable = new CompositeDisposable();
        this.application = application;
    }


    void getItems(int listId) {
        this.listId = listId;

        disposable.add(model.getListItems(listId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(userListsSubscriber())
        );
    }

    private DisposableSubscriber<List<Item>> userListsSubscriber() {
        return new DisposableSubscriber<List<Item>>() {
            @Override
            public void onNext(List<Item> items) {
                ItemViewModel.this.itemList.setValue(items);
            }

            @Override
            public void onError(Throwable t) {
                Timber.e(t);
                ItemViewModel.this.error.setValue(application.getString(R.string.error_msg_generic));
            }

            @Override
            public void onComplete() {
            }
        };
    }


    void add(String title, int position, int listId) {
        Completable.fromAction(() -> model.addItem(new Item(title, position, listId)))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    void delete(int itemId) {
        Completable.fromAction(() -> model.deleteItem(itemId))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    void changeTitle(int itemId, String newTitle) {
        Completable.fromAction(() -> model.changeItemTitle(itemId, newTitle))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    LiveData<List<Item>> getItemList() {
        return itemList;
    }

    LiveData<String> getError() {
        return error;
    }

    int getListId() {
        return listId;
    }
}

package com.example.david.lists.ui.detail;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.datamodel.Item;
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

final class DetailViewModel extends AndroidViewModel {

    private final int listId;
    private final MutableLiveData<List<Item>> itemList;
    private final MutableLiveData<String> error;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    DetailViewModel(@NonNull Application application, int listId) {
        super(application);
        this.listId = listId;
        itemList = new MutableLiveData<>();
        error = new MutableLiveData<>();
        model = Model.getInstance(application);
        disposable = new CompositeDisposable();

        getItems();
    }


    private void getItems() {
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
                Timber.i("onNext");
                DetailViewModel.this.itemList.setValue(items);
            }

            @Override
            public void onError(Throwable t) {
                Timber.e(t);
                DetailViewModel.this.error.setValue(getApplication().getString(R.string.error_msg_generic));
            }

            @Override
            public void onComplete() {
                Timber.i("onComplete");
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

    void prepareToDelete(Item item) {

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


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    void undoDeletion() {

    }

    void permanentlyDelete() {

    }
}

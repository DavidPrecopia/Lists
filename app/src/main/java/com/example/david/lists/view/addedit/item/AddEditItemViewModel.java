package com.example.david.lists.view.addedit.item;

import android.app.Application;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.addedit.AddEditViewModelBase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subscribers.DisposableSubscriber;

public class AddEditItemViewModel extends AddEditViewModelBase {

    private final String id;
    private final String userListId;

    // This is used to acquire the last position if adding.
    private final List<Item> items;

    public AddEditItemViewModel(Application application,
                                IRepository repository,
                                String id,
                                String currentTitle,
                                String userListId) {
        super(application, repository, currentTitle);
        this.id = id;
        this.userListId = userListId;
        this.items = new ArrayList<>();
        init();
    }

    private void init() {
        disposable.add(repository.getItems(userListId)
                .subscribeWith(userListsSubscriber())
        );
    }

    /**
     * Observing the repository in case a new UserList is added while the user
     * is adding a UserList.
     */
    private DisposableSubscriber<List<Item>> userListsSubscriber() {
        return new DisposableSubscriber<List<Item>>() {
            @Override
            public void onNext(List<Item> newItems) {
                items.clear();
                items.addAll(newItems);
            }

            @Override
            public void onError(Throwable t) {
                UtilExceptions.throwException(t);
            }

            @Override
            public void onComplete() {
            }
        };
    }


    @Override
    public void save(String newTitle) {
        if (getTaskType() == TASK_ADD) {
            repository.addItem(new Item(newTitle, this.items.size(), this.userListId));
        } else if (getTaskType() == TASK_EDIT) {
            repository.renameItem(id, newTitle);
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
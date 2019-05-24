package com.example.david.lists.ui.addedit.userlist;

import android.app.Application;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.ui.addedit.AddEditViewModelBase;
import com.example.david.lists.util.UtilExceptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public class AddEditUserListViewModel extends AddEditViewModelBase {

    private final String id;

    // This is used to acquire the last position if adding.
    private final List<UserList> userLists;

    private final IRepository repository;
    private final CompositeDisposable disposable;

    public AddEditUserListViewModel(Application application,
                                    IRepository repository,
                                    String id,
                                    String currentTitle) {
        super(application, currentTitle);
        this.id = id;
        this.userLists = new ArrayList<>();
        this.repository = repository;
        this.disposable = new CompositeDisposable();
        init();
    }

    private void init() {
        disposable.add(repository.getAllUserLists()
                .subscribeWith(userListsSubscriber())
        );
    }

    /**
     * Observing the repository in case a new UserList is added while the user
     * is adding a UserList.
     */
    private DisposableSubscriber<List<UserList>> userListsSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> newUserLists) {
                userLists.clear();
                userLists.addAll(newUserLists);
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
            repository.addUserList(new UserList(newTitle, this.userLists.size()));
        } else if (getTaskType() == TASK_EDIT) {
            repository.renameUserList(this.id, newTitle);
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}

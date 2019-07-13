package com.example.david.lists.view.addedit.userlist;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.addedit.common.AddEditLogicBase;
import com.example.david.lists.view.addedit.common.IAddEditContract;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_ADD;
import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_EDIT;

public class AddEditUserListLogic extends AddEditLogicBase {

    public AddEditUserListLogic(IAddEditContract.View view,
                                IAddEditContract.ViewModel viewModel,
                                IRepositoryContract.Repository repository,
                                ISchedulerProviderContract schedulerProvide,
                                CompositeDisposable disposable,
                                String id,
                                String title) {
        super(view, viewModel, repository, schedulerProvide, disposable, id, title, null);
        getUserList();
    }

    private void getUserList() {
        disposable.add(repository.getAllUserLists()
                .subscribeWith(userListsSubscriber())
        );
    }

    private DisposableSubscriber<List<UserList>> userListsSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> newUserLists) {
                viewModel.setLastPosition(newUserLists.size());
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
        if (viewModel.getCurrentTaskType() == TASK_ADD) {
            repository.addUserList(new UserList(newTitle, viewModel.getLastPosition()));
        } else if (viewModel.getCurrentTaskType() == TASK_EDIT) {
            repository.renameUserList(viewModel.getId(), newTitle);
        }
    }
}

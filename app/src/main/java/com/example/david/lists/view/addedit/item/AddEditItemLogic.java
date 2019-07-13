package com.example.david.lists.view.addedit.item;

import com.example.david.lists.data.datamodel.Item;
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

public class AddEditItemLogic extends AddEditLogicBase {

    public AddEditItemLogic(IAddEditContract.View view,
                            IAddEditContract.ViewModel viewModel,
                            IRepositoryContract.Repository repository,
                            ISchedulerProviderContract schedulerProvide,
                            CompositeDisposable disposable,
                            String id,
                            String title,
                            String userListId) {
        super(view, viewModel, repository, schedulerProvide, disposable, id, title, userListId);
        getItemsList();
    }

    private void getItemsList() {
        disposable.add(repository.getItems(viewModel.getUserListId())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(itemsSubscriber())
        );
    }

    private DisposableSubscriber<List<Item>> itemsSubscriber() {
        return new DisposableSubscriber<List<Item>>() {
            @Override
            public void onNext(List<Item> newItems) {
                viewModel.setLastPosition(newItems.size());
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
            repository.addItem(new Item(newTitle, viewModel.getLastPosition(), viewModel.getUserListId()));
        } else if (viewModel.getCurrentTaskType() == TASK_EDIT) {
            repository.renameItem(viewModel.getId(), newTitle);
        }
    }
}

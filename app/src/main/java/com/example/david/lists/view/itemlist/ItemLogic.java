package com.example.david.lists.view.itemlist;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.util.UtilExceptions;

import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public final class ItemLogic implements IItemViewContract.Logic {

    private final IItemViewContract.View view;
    private final IItemViewContract.ViewModel viewModel;
    private final IItemViewContract.Adapter adapter;

    private final IRepositoryContract.Repository repository;
    private final ISchedulerProviderContract schedulerProvider;
    private final CompositeDisposable disposable;

    private Observer<List<UserList>> repositoryObserver;

    public ItemLogic(IItemViewContract.View view,
                     IItemViewContract.ViewModel viewModel,
                     IItemViewContract.Adapter adapter,
                     IRepositoryContract.Repository repository,
                     ISchedulerProviderContract schedulerProvider,
                     CompositeDisposable disposable) {
        this.view = view;
        this.viewModel = viewModel;
        this.adapter = adapter;
        this.repository = repository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = disposable;

        adapter.init(this);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return (RecyclerView.Adapter) adapter;
    }


    @Override
    public void onStart() {
        view.setStateLoading();
        observeModel();
        getItems();
    }


    private void observeModel() {
        repositoryObserver = userLists -> {
            for (UserList userList : userLists) {
                if (userList.getId().equals(viewModel.getUserListId())) {
                    view.showMessage(viewModel.getMsgListDeleted(userList.getTitle()));
                    view.finishView();
                }
            }
        };
        repository.getEventUserListDeleted().observeForever(repositoryObserver);
    }

    private void getItems() {
        disposable.add(repository.getItems(viewModel.getUserListId())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(itemSubscriber())
        );
    }

    private DisposableSubscriber<List<Item>> itemSubscriber() {
        return new DisposableSubscriber<List<Item>>() {
            @Override
            public void onNext(List<Item> itemList) {
                viewModel.setViewData(itemList);
                evaluateNewData();
            }

            @Override
            public void onError(Throwable t) {
                view.setStateError(viewModel.getErrorMsg());
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private void evaluateNewData() {
        List<Item> viewData = viewModel.getViewData();
        adapter.submitList(viewData);
        if (viewData.isEmpty()) {
            view.setStateError(viewModel.getErrorMsgEmptyList());
        } else {
            view.setStateDisplayList();
        }
    }

    @Override
    public void addButtonClicked() {
        view.openAddDialog(viewModel.getUserListId(), viewModel.getViewData().size());
    }

    @Override
    public void edit(int position) {
        view.openEditDialog(viewModel.getViewData().get(position));
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        Collections.swap(viewModel.getViewData(), fromPosition, toPosition);
        adapter.move(fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        if (newPosition < 0) {
            return;
        }

        Item item = viewModel.getViewData().get(newPosition);
        repository.updateItemPosition(
                item,
                item.getPosition(),
                newPosition
        );
    }


    @Override
    public void delete(int position) {
        adapter.remove(position);
        saveDeletedItem(position);

        view.notifyUserOfDeletion(viewModel.getMsgItemDeleted());
    }

    private void saveDeletedItem(int position) {
        viewModel.getTempList().add(viewModel.getViewData().get(position));
        viewModel.setTempPosition(position);
        viewModel.getViewData().remove(position);
    }


    @Override
    public void undoRecentDeletion() {
        if (viewModel.getTempList().isEmpty() || viewModel.getTempPosition() < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    viewModel.getErrorMsgInvalidUndo()
            ));
        }
        reAdd();
        deletionNotificationTimedOut();
    }

    private void reAdd() {
        int lastDeletedPosition = (viewModel.getTempList().size() - 1);
        reAddItemToAdapter(lastDeletedPosition);
        reAddItemToLocalList(lastDeletedPosition);
        viewModel.getTempList().remove(lastDeletedPosition);
    }

    private void reAddItemToAdapter(int lastDeletedPosition) {
        adapter.reAdd(lastDeletedPosition, viewModel.getTempList().get(lastDeletedPosition));
    }

    private void reAddItemToLocalList(int lastDeletedPosition) {
        viewModel.getViewData().add(
                lastDeletedPosition,
                viewModel.getTempList().get(lastDeletedPosition)
        );
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (viewModel.getTempList().isEmpty()) {
            return;
        }
        repository.deleteItems(viewModel.getTempList());
        viewModel.getTempList().clear();
    }


    @Override
    public void onDestroy() {
        disposable.clear();
        repository.getEventUserListDeleted().removeObserver(repositoryObserver);
    }
}

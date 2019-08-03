package com.example.david.lists.view.itemlist;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.common.ListViewLogicBase;

import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public final class ItemListLogic extends ListViewLogicBase
        implements IItemViewContract.Logic {

    private final IItemViewContract.View view;
    private final IItemViewContract.ViewModel viewModel;

    public ItemListLogic(IItemViewContract.View view,
                         IItemViewContract.ViewModel viewModel,
                         IRepositoryContract.Repository repo,
                         ISchedulerProviderContract schedulerProvider,
                         CompositeDisposable disposable) {
        super(repo, schedulerProvider, disposable);
        this.view = view;
        this.viewModel = viewModel;
    }

    @Override
    public void onStart() {
        view.setStateLoading();
        observeDeletedUserLists();
        getItems();
    }


    private void observeDeletedUserLists() {
        disposable.add(repo.getEventUserListDeleted()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(userLists -> {
                            for (UserList userList : userLists) {
                                if (userList.getId().equals(viewModel.getUserListId())) {
                                    view.showMessage(viewModel.getMsgListDeleted(userList.getTitle()));
                                    view.finishView();
                                }
                            }
                        },
                        UtilExceptions::throwException)
        );
    }

    private void getItems() {
        disposable.add(repo.getItems(viewModel.getUserListId())
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
        view.submitList(viewData);
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
    public void dragging(int fromPosition, int toPosition, IItemViewContract.Adapter adapter) {
        Collections.swap(viewModel.getViewData(), fromPosition, toPosition);
        adapter.move(fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        if (newPosition < 0) {
            return;
        }

        Item item = viewModel.getViewData().get(newPosition);
        repo.updateItemPosition(
                item,
                item.getPosition(),
                newPosition
        );
    }


    @Override
    public void delete(int position, IItemViewContract.Adapter adapter) {
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
    public void undoRecentDeletion(IItemViewContract.Adapter adapter) {
        if (viewModel.getTempList().isEmpty() || viewModel.getTempPosition() < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    viewModel.getErrorMsgInvalidUndo()
            ));
        }
        reAdd(adapter);
        deletionNotificationTimedOut();
    }

    private void reAdd(IItemViewContract.Adapter adapter) {
        int lastDeletedPosition = (viewModel.getTempList().size() - 1);
        reAddItemToAdapter(lastDeletedPosition, adapter);
        reAddItemToLocalList(lastDeletedPosition);
        viewModel.getTempList().remove(lastDeletedPosition);
    }

    private void reAddItemToAdapter(int lastDeletedPosition, IItemViewContract.Adapter adapter) {
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
        repo.deleteItems(viewModel.getTempList());
        viewModel.getTempList().clear();
    }


    @Override
    public void onDestroy() {
        disposable.clear();
    }
}

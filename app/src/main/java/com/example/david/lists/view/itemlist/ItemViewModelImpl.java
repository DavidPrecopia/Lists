package com.example.david.lists.view.itemlist;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.common.ViewModelBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.subscribers.DisposableSubscriber;

public final class ItemViewModelImpl extends ViewModelBase
        implements IItemViewModel {

    private final String userListId;
    private final MutableLiveData<List<Item>> itemList;

    private final SingleLiveEvent<Item> eventEdit;
    private final SingleLiveEvent<Void> eventFinish;

    private Observer<List<UserList>> repositoryObserver;

    private final List<Item> tempItemList;
    private int tempItemPosition;

    public ItemViewModelImpl(@NonNull Application application, IRepository repository, String userListId) {
        super(application, repository);
        this.userListId = userListId;
        itemList = new MutableLiveData<>();
        eventEdit = new SingleLiveEvent<>();
        eventFinish = new SingleLiveEvent<>();
        tempItemList = new ArrayList<>();
        tempItemPosition = -1;

        init();
    }


    private void init() {
        observeModel();
        getItems();
    }


    private void observeModel() {
        repositoryObserver = userLists -> {
            for (UserList userList : userLists) {
                if (userList.getId().equals(this.userListId)) {
                    Toast.makeText(getApplication(),
                            getStringResource(R.string.message_user_list_deletion_parameter, userList.getTitle()),
                            Toast.LENGTH_SHORT
                    ).show();
                    eventFinish.call();
                }
            }
        };
        repository.getEventUserListDeleted().observeForever(repositoryObserver);
    }

    private void getItems() {
        disposable.add(repository.getItems(userListId)
                .subscribeWith(itemSubscriber())
        );
    }

    private DisposableSubscriber<List<Item>> itemSubscriber() {
        return new DisposableSubscriber<List<Item>>() {
            @Override
            public void onNext(List<Item> itemList) {
                ItemViewModelImpl.this.itemList.setValue(itemList);
                evaluateNewData(itemList);
            }

            @Override
            public void onError(Throwable t) {
                UtilExceptions.throwException(t);
                errorMessage.setValue(getStringResource(R.string.error_msg_generic));
            }

            @Override
            public void onComplete() {
            }
        };
    }


    @Override
    public void addButtonClicked() {
        eventAdd.setValue(userListId);
    }

    @Override
    public void edit(Item item) {
        eventEdit.setValue(item);
    }


    @Override
    public void dragging(IItemAdapter adapter, int fromPosition, int toPosition) {
        Collections.swap(itemList.getValue(), fromPosition, toPosition);
        adapter.move(fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        if (newPosition < 0) {
            return;
        }

        Item item = itemList.getValue().get(newPosition);
        repository.updateItemPosition(
                item,
                item.getPosition(),
                newPosition
        );
    }

    @Override
    public void swipedLeft(IItemAdapter adapter, int position) {
        delete(adapter, position);
    }


    @Override
    public void delete(IItemAdapter adapter, int position) {
        adapter.remove(position);
        saveDeletedItem(position);

        eventNotifyUserOfDeletion.setValue(
                getStringResource(R.string.message_item_deletion)
        );
    }

    private void saveDeletedItem(int position) {
        tempItemList.add(itemList.getValue().get(position));
        tempItemPosition = position;
        itemList.getValue().remove(position);
    }


    @Override
    public void undoRecentDeletion(IItemAdapter adapter) {
        if (tempItemList == null || tempItemPosition < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    getStringResource(R.string.error_invalid_action_undo_deletion)
            ));
        }
        reAdd(adapter);
        deletionNotificationTimedOut();
    }

    private void reAdd(IItemAdapter adapter) {
        int lastDeletedPosition = (tempItemList.size() - 1);
        reAddItemToAdapter(adapter, lastDeletedPosition);
        reAddItemToLocalList(lastDeletedPosition);
        tempItemList.remove(lastDeletedPosition);
    }

    private void reAddItemToAdapter(IItemAdapter adapter, int lastDeletedPosition) {
        adapter.reAdd(tempItemPosition, tempItemList.get(lastDeletedPosition));
    }

    private void reAddItemToLocalList(int lastDeletedPosition) {
        itemList.getValue().add(tempItemPosition, tempItemList.get(lastDeletedPosition));
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (tempItemList.isEmpty()) {
            return;
        }
        repository.deleteItems(tempItemList);
        tempItemList.clear();
    }


    @Override
    public LiveData<List<Item>> getItemList() {
        List<Item> value = itemList.getValue();
        if (value != null) {
            evaluateNewData(value);
        }
        return itemList;
    }

    @Override
    public LiveData<Boolean> getEventDisplayLoading() {
        return eventDisplayLoading;
    }

    @Override
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    public LiveData<Boolean> getEventDisplayError() {
        return eventDisplayError;
    }

    @Override
    public LiveData<String> getEventNotifyUserOfDeletion() {
        return eventNotifyUserOfDeletion;
    }

    @Override
    public LiveData<String> getEventAdd() {
        return eventAdd;
    }

    @Override
    public LiveData<Item> getEventEdit() {
        return eventEdit;
    }

    @Override
    public LiveData<Void> getEventFinish() {
        return eventFinish;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        repository.getEventUserListDeleted().removeObserver(repositoryObserver);
    }
}

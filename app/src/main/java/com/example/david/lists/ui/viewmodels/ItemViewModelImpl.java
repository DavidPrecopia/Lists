package com.example.david.lists.ui.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.ui.adapaters.IItemAdapterContract;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilExceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public final class ItemViewModelImpl extends AndroidViewModel implements IItemViewModel {

    private final String userListId;
    private final MutableLiveData<List<Item>> itemList;

    private final IRepository repository;
    private final CompositeDisposable disposable;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final SingleLiveEvent<Boolean> eventDisplayError;
    private final SingleLiveEvent<String> errorMessage;
    private final SingleLiveEvent<String> eventNotifyUserOfDeletion;
    private final SingleLiveEvent<String> eventAdd;
    private final SingleLiveEvent<EditingInfo> eventEdit;
    private final SingleLiveEvent<Void> eventFinish;

    private Observer<List<UserList>> repositoryObserver;

    private final List<Item> tempItemList;
    private int tempItemPosition;

    ItemViewModelImpl(@NonNull Application application, IRepository repository, String userListId) {
        super(application);
        this.userListId = userListId;
        itemList = new MutableLiveData<>();
        this.repository = repository;
        disposable = new CompositeDisposable();
        eventDisplayLoading = new MutableLiveData<>();
        eventDisplayError = new SingleLiveEvent<>();
        errorMessage = new SingleLiveEvent<>();
        eventNotifyUserOfDeletion = new SingleLiveEvent<>();
        eventAdd = new SingleLiveEvent<>();
        eventEdit = new SingleLiveEvent<>();
        eventFinish = new SingleLiveEvent<>();
        tempItemList = new ArrayList<>();
        tempItemPosition = -1;

        init();
    }


    private void init() {
        eventDisplayLoading.setValue(true);
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
                .subscribeWith(userListsSubscriber())
        );
    }

    private DisposableSubscriber<List<Item>> userListsSubscriber() {
        return new DisposableSubscriber<List<Item>>() {
            @Override
            public void onNext(List<Item> itemList) {
                ItemViewModelImpl.this.itemList.setValue(itemList);
                evaluateNewData(itemList);
            }

            @SuppressLint("LogNotTimber")
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

    private void evaluateNewData(List<Item> newItemList) {
        eventDisplayLoading.setValue(false);

        if (newItemList.isEmpty()) {
            errorMessage.setValue(getStringResource(R.string.error_msg_empty_user_list));
            eventDisplayError.setValue(true);
        } else {
            eventDisplayError.setValue(false);
        }
    }


    @Override
    public void addButtonClicked() {
        eventAdd.setValue(getStringResource(R.string.hint_add_item));
    }

    @Override
    public void add(String title) {
        repository.addItem(new Item(title, itemList.getValue().size(), this.userListId));
    }


    @Override
    public void edit(Item item) {
        eventEdit.setValue(new EditingInfo(item));
    }

    @Override
    public void changeTitle(EditingInfo editingInfo, String newTitle) {
        repository.renameItem(editingInfo.getId(), newTitle);
    }


    @Override
    public void dragging(IItemAdapterContract adapter, int fromPosition, int toPosition) {
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
    public void swipedLeft(IItemAdapterContract adapter, int position) {
        delete(adapter, position);
    }


    @Override
    public void delete(IItemAdapterContract adapter, int position) {
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
    public void undoRecentDeletion(IItemAdapterContract adapter) {
        if (tempItemList == null || tempItemPosition < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    getStringResource(R.string.error_invalid_action_undo_deletion)
            ));
        }
        reAdd(adapter);
        deletionNotificationTimedOut();
    }

    private void reAdd(IItemAdapterContract adapter) {
        int lastDeletedPosition = (tempItemList.size() - 1);
        reAddItemToAdapter(adapter, lastDeletedPosition);
        reAddItemToLocalList(lastDeletedPosition);
        tempItemList.remove(lastDeletedPosition);
    }

    private void reAddItemToAdapter(IItemAdapterContract adapter, int lastDeletedPosition) {
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
    public LiveData<EditingInfo> getEventEdit() {
        return eventEdit;
    }

    @Override
    public LiveData<Void> getEventFinish() {
        return eventFinish;
    }


    private String getStringResource(int resId) {
        return getApplication().getString(resId);
    }

    private String getStringResource(int resId, Object object) {
        return getApplication().getString(resId, object);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        repository.getEventUserListDeleted().removeObserver(repositoryObserver);
    }
}

package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.adapaters.ItemsAdapter;
import com.example.david.lists.ui.view.ItemTouchHelperCallback;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

import static com.example.david.lists.util.UtilRxJava.completableIoAccess;

public final class ItemViewModel extends AndroidViewModel
        implements IViewModelContract,
        ItemTouchHelperCallback.IStartDragListener {

    private final String listId;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final List<Item> itemList;
    private final ItemsAdapter adapter;
    private final ItemTouchHelper touchHelper;

    private final MutableLiveData<String> toolbarTitle;
    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final SingleLiveEvent<String> eventDisplayError;
    private final SingleLiveEvent<String> eventNotifyUserOfDeletion;
    private final SingleLiveEvent<String> eventAdd;
    private final SingleLiveEvent<EditingInfo> eventEdit;

    private Observer<List<UserList>> modelObserver;
    private final SingleLiveEvent<Void> eventFinish;

    private List<Item> tempItemList;
    private int tempItemPosition = -1;

    public ItemViewModel(@NonNull Application application, IModelContract model, String listId, String listTitle) {
        super(application);
        this.listId = listId;
        itemList = new ArrayList<>();
        this.model = model;
        disposable = new CompositeDisposable();
        adapter = new ItemsAdapter(this, this);
        touchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(this));
        toolbarTitle = new MutableLiveData<>();
        eventDisplayLoading = new MutableLiveData<>();
        eventDisplayError = new SingleLiveEvent<>();
        eventNotifyUserOfDeletion = new SingleLiveEvent<>();
        eventAdd = new SingleLiveEvent<>();
        eventEdit = new SingleLiveEvent<>();
        tempItemList = new ArrayList<>();
        eventFinish = new SingleLiveEvent<>();

        init(listTitle);
    }


    private void init(String listTitle) {
        eventDisplayLoading.setValue(true);
        toolbarTitle.setValue(listTitle);
        observeModel();
        getItems();
    }


    private void observeModel() {
        modelObserver = userLists -> {
            for (UserList userList : userLists) {
                if (userList.getId().equals(this.listId)) {
                    Timber.i("Listening to User ID: %s", userList.getId());
                    eventFinish.call();
                }
            }
        };
        model.getEventUserListDeleted().observeForever(modelObserver);
    }

    private void getItems() {
        disposable.add(model.getUserListItems(listId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(userListsSubscriber())
        );
    }

    private DisposableSubscriber<List<Item>> userListsSubscriber() {
        return new DisposableSubscriber<List<Item>>() {
            @Override
            public void onNext(List<Item> itemList) {
                updaterItemsList(itemList);
                updateUi();
            }

            @Override
            public void onError(Throwable t) {
                Timber.e(t);
                eventDisplayError.setValue(
                        getStringResource(R.string.error_msg_generic)
                );
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private void updaterItemsList(List<Item> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
    }

    private void updateUi() {
        if (UtilUser.signedOut()) {
            return;
        }

        if (itemList.isEmpty()) {
            eventDisplayError.setValue(
                    getStringResource(R.string.error_msg_empty_list)
            );
        } else {
            eventDisplayLoading.setValue(false);
        }
        adapter.swapData(itemList);
    }


    @Override
    public void userListClicked(UserList userList) {
        cannotOpenUserListException();
    }


    @Override
    public void addButtonClicked() {
        eventAdd.setValue(getStringResource(R.string.hint_add_item));
    }

    @Override
    public void add(String title) {
        completableIoAccess(Completable.fromAction(() ->
                model.addItem(new Item(title, itemList.size(), this.listId)))
        );
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        Collections.swap(itemList, fromPosition, toPosition);
        adapter.move(fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        Item item = itemList.get(newPosition);
        completableIoAccess(Completable.fromAction(() ->
                model.updateItemPosition(
                        item,
                        item.getPosition(),
                        newPosition
                ))
        );
    }


    @Override
    public void edit(int position) {
        eventEdit.setValue(new EditingInfo(itemList.get(position)));
    }

    @Override
    public void changeTitle(String idemId, String newTitle) {
        completableIoAccess(Completable.fromAction(() ->
                model.renameItem(idemId, newTitle))
        );
    }


    @Override
    public void delete(int position) {
        adapter.remove(position);
        tempItemList.add(itemList.get(position));
        tempItemPosition = position;

        eventNotifyUserOfDeletion.setValue(
                getStringResource(R.string.message_item_deletion)
        );
    }

    @Override
    public void swipedLeft(int position) {
        delete(position);
    }

    @Override
    public void undoRecentDeletion() {
        if (tempItemList == null || tempItemPosition < 0) {
            throw new UnsupportedOperationException(
                    getStringResource(R.string.error_invalid_action_undo_deletion)
            );
        }
        reAdd();
    }

    private void reAdd() {
        adapter.reAdd(
                tempItemPosition,
                tempItemList.get(tempItemPosition)
        );
        tempItemList.remove(tempItemList.size() - 1);
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (tempItemList.isEmpty()) {
            return;
        }
        List<Item> items = new ArrayList<>(tempItemList);
        completableIoAccess(Completable.fromAction(() ->
                model.deleteItems(items)
        ));
        tempItemList.clear();
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }


    @Override
    public void signIn() {
        throwUnsupportedOperation("");
    }

    @Override
    public void signOut() {
        throwUnsupportedOperation("");
    }

    @Override
    public void successfullySignedOut() {
        throwUnsupportedOperation("");
    }


    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public ItemTouchHelper getItemTouchHelper() {
        return touchHelper;
    }

    @Override
    public LiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    @Override
    public LiveData<UserList> getEventOpenUserList() {
        cannotOpenUserListException();
        return null;
    }

    @Override
    public LiveData<Boolean> getEventDisplayLoading() {
        return eventDisplayLoading;
    }

    @Override
    public LiveData<String> getEventDisplayError() {
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
    public LiveData<Void> getEventSignOut() {
        throwUnsupportedOperation("");
        return null;
    }

    @Override
    public LiveData<Void> getEventSignIn() {
        throwUnsupportedOperation("");
        return null;
    }

    @Override
    public LiveData<Void> getEventFinish() {
        return eventFinish;
    }


    private void cannotOpenUserListException() {
        throwUnsupportedOperation(
                getStringResource(R.string.error_cannot_open_user_list)
        );
    }

    private void throwUnsupportedOperation(String message) {
        throw new UnsupportedOperationException(message);
    }

    private String getStringResource(int resId) {
        return getApplication().getString(resId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        model.getEventUserListDeleted().removeObserver(modelObserver);
    }
}

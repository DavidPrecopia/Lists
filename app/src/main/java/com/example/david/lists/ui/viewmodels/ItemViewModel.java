package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.model.IModelContract;
import com.example.david.lists.ui.adapaters.ItemsAdapter;
import com.example.david.lists.ui.dialogs.EditingInfo;
import com.example.david.lists.ui.view.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    private final int listId;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final List<Item> itemList;
    private final ItemsAdapter adapter;
    private final ItemTouchHelper touchHelper;


    private final MutableLiveData<String> toolbarTitle;
    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final MutableLiveData<String> eventDisplayError;
    private final MutableLiveData<String> eventNotifyUserOfDeletion;
    private final MutableLiveData<String> eventAdd;
    private final MutableLiveData<EditingInfo> eventEdit;

    private Item temporaryItem;
    private int temporaryItemPosition = -1;

    public ItemViewModel(@NonNull Application application, IModelContract model, int listId, String listTitle) {
        super(application);
        this.listId = listId;
        itemList = new ArrayList<>();
        this.model = model;
        disposable = new CompositeDisposable();
        adapter = new ItemsAdapter(this, this);
        touchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(this));
        toolbarTitle = new MutableLiveData<>();
        eventDisplayLoading = new MutableLiveData<>();
        eventDisplayError = new MutableLiveData<>();
        eventNotifyUserOfDeletion = new MutableLiveData<>();
        eventAdd = new MutableLiveData<>();
        eventEdit = new MutableLiveData<>();

        init(listTitle);
    }


    private void init(String listTitle) {
        eventDisplayLoading.setValue(true);
        toolbarTitle.setValue(listTitle);
        getItems();
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
        if (itemList.isEmpty()) {
            eventDisplayError.setValue(
                    getStringResource(R.string.error_msg_empty_list)
            );
        } else {
            adapter.swapData(itemList);
            eventDisplayLoading.setValue(false);
        }
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
                model.addItem(new Item(title, adapter.getItemCount(), this.listId)))
        );
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        adapter.move(fromPosition, toPosition);
    }

    @Override
    public void movePermanently(int newPosition) {
        Item item = itemList.get(newPosition);
        model.moveItemPosition(
                item.getId(),
                item.getPosition(),
                newPosition
        );
    }


    @Override
    public void edit(int position) {
        eventEdit.setValue(new EditingInfo(itemList.get(position)));
    }

    @Override
    public void changeTitle(int idemId, String newTitle) {
        completableIoAccess(Completable.fromAction(() ->
                model.changeItemTitle(idemId, newTitle))
        );
    }


    @Override
    public void delete(int position) {
        adapter.remove(position);
        temporaryItem = itemList.get(position);
        temporaryItemPosition = position;

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
        if (temporaryItem == null || temporaryItemPosition < 0) {
            throw new UnsupportedOperationException(
                    getStringResource(R.string.error_invalid_deletion_undo)
            );
        }

        adapter.reAdd(temporaryItemPosition, temporaryItem);
        clearTemporary();
    }

    @Override
    public void deletionNotificationTimedOut() {
        // There is a possibility that temporaryItem is nullified,
        // before fromAction executes.
        int id = temporaryItem.getId();
        completableIoAccess(Completable.fromAction(() ->
                model.deleteItem(id))
        );
        clearTemporary();
    }

    private void clearTemporary() {
        temporaryItem = null;
        temporaryItemPosition = -1;
    }


    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }


    @Override
    public void refresh() {
        Timber.i("refresh");
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


    private void cannotOpenUserListException() {
        throw new UnsupportedOperationException(
                getStringResource(R.string.error_cannot_open_user_list)
        );
    }

    private String getStringResource(int resId) {
        return getApplication().getString(resId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}

package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.BuildConfig;
import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.adapaters.ItemsAdapter;
import com.example.david.lists.ui.view.TouchHelperCallback;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilRecyclerView;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

public final class ItemViewModel extends AndroidViewModel
        implements IItemViewModelContract,
        TouchHelperCallback.TouchCallback,
        TouchHelperCallback.IStartDragListener,
        UtilRecyclerView.PopUpMenuCallback {

    private final String groupId;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final List<Item> itemList;
    private final ItemsAdapter adapter;
    private final ItemTouchHelper touchHelper;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final SingleLiveEvent<String> eventDisplayError;
    private final SingleLiveEvent<String> eventNotifyUserOfDeletion;
    private final SingleLiveEvent<String> eventAdd;
    private final SingleLiveEvent<EditingInfo> eventEdit;

    private Observer<List<Group>> modelObserver;
    private final SingleLiveEvent<Void> eventFinish;

    private final List<Item> tempItemList;
    private int tempItemPosition = -1;

    public ItemViewModel(@NonNull Application application, IModelContract model, String groupId) {
        super(application);
        this.groupId = groupId;
        itemList = new ArrayList<>();
        this.model = model;
        disposable = new CompositeDisposable();
        adapter = new ItemsAdapter(this, this);
        touchHelper = new ItemTouchHelper(new TouchHelperCallback(this));
        eventDisplayLoading = new MutableLiveData<>();
        eventDisplayError = new SingleLiveEvent<>();
        eventNotifyUserOfDeletion = new SingleLiveEvent<>();
        eventAdd = new SingleLiveEvent<>();
        eventEdit = new SingleLiveEvent<>();
        tempItemList = new ArrayList<>();
        eventFinish = new SingleLiveEvent<>();

        init();
    }


    private void init() {
        eventDisplayLoading.setValue(true);
        observeModel();
        getItems();
    }


    private void observeModel() {
        modelObserver = userLists -> {
            for (Group group : userLists) {
                if (group.getId().equals(this.groupId)) {
                    Timber.i("Listening to User ID: %s", group.getId());
                    eventFinish.call();
                }
            }
        };
        model.getEventGroupDeleted().observeForever(modelObserver);
    }

    private void getItems() {
        disposable.add(model.getGroupItems(groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(userListsSubscriber())
        );
    }

    private DisposableSubscriber<List<Item>> userListsSubscriber() {
        return new DisposableSubscriber<List<Item>>() {
            @Override
            public void onNext(List<Item> itemList) {
                if (BuildConfig.DEBUG) Timber.i("onNext");
                updaterItemsList(itemList);
                updateUi();
            }

            @Override
            public void onError(Throwable t) {
                if (BuildConfig.DEBUG) Timber.e(t);
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
                    getStringResource(R.string.error_msg_empty_group)
            );
        } else {
            eventDisplayLoading.setValue(false);
        }
        adapter.swapData(itemList);
    }


    @Override
    public void addButtonClicked() {
        eventAdd.setValue(getStringResource(R.string.hint_add_item));
    }

    @Override
    public void add(String title) {
        model.addItem(new Item(title, itemList.size(), this.groupId));
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        Collections.swap(itemList, fromPosition, toPosition);
        adapter.move(fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        Item item = itemList.get(newPosition);
        model.updateItemPosition(
                item,
                item.getPosition(),
                newPosition
        );
    }


    @Override
    public void edit(int position) {
        eventEdit.setValue(new EditingInfo(itemList.get(position)));
    }

    @Override
    public void changeTitle(EditingInfo editingInfo, String newTitle) {
        model.renameItem(editingInfo.getId(), newTitle);
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
        model.deleteItems(items);
        tempItemList.clear();
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
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
    public LiveData<Void> getEventFinish() {
        return new LiveData<Void>() {
        };
    }



    private String getStringResource(int resId) {
        return getApplication().getString(resId);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        model.getEventGroupDeleted().removeObserver(modelObserver);
    }
}

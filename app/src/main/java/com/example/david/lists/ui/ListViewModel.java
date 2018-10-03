package com.example.david.lists.ui;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public final class ListViewModel extends AndroidViewModel {

    private final MutableLiveData<String> toolbarTitle;
    private final MutableLiveData<Boolean> displayLoading;
    private final MutableLiveData<RecyclerView.Adapter> recyclerViewAdapter;
    private final MutableLiveData<String> errorLiveData;
    private final SingleLiveEvent<Void> eventNotifyUserOfDeletion;

    private final List<UserList> userLists;
    private final List<Item> itemList;

    private final UserListViewModel userListViewModel;
    private Observer<List<UserList>> userListObserver;
    private Observer<String> userListErrorObserver;

    private final ItemViewModel itemViewModel;
    private Observer<List<Item>> itemObserver;
    private Observer<String> itemErrorObserver;

    private final UserListsAdapter userListsAdapter;
    private final ItemsAdapter itemsAdapter;

    private UserList temporaryUserList;
    private int temporaryUserListPosition = -1;
    private Item temporaryItem;
    private int temporaryItemPosition = -1;

    private static int currentlyDisplayed;
    private static final int USER_LISTS = 100;
    private static final int ITEMS = 200;

    public ListViewModel(@NonNull Application application) {
        super(application);
        toolbarTitle = new MutableLiveData<>();
        displayLoading = new MutableLiveData<>();
        recyclerViewAdapter = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        eventNotifyUserOfDeletion = new SingleLiveEvent<>();
        userLists = new ArrayList<>();
        itemList = new ArrayList<>();
        userListViewModel = new UserListViewModel(application);
        itemViewModel = new ItemViewModel(application);
        userListsAdapter = new UserListsAdapter(this);
        itemsAdapter = new ItemsAdapter();

        init();
    }


    private void init() {
        currentlyDisplayed = USER_LISTS;
        recyclerViewAdapter.setValue(userListsAdapter);
        changeTitle(getApplication().getString(R.string.app_name));
        displayLoading.setValue(true);

        observeData();
        observerErrors();
    }

    private void observeData() {
        this.userListObserver = initUserListsObserver();
        this.itemObserver = initItemObserver();
        userListViewModel.getUserLists().observeForever(userListObserver);
        itemViewModel.getItemList().observeForever(itemObserver);
    }

    private void observerErrors() {
        this.userListErrorObserver = initUserListErrorObserver();
        this.itemErrorObserver = initItemErrorObserver();

        userListViewModel.getError().observeForever(userListErrorObserver);
        itemViewModel.getError().observeForever(itemErrorObserver);
    }

    private Observer<List<UserList>> initUserListsObserver() {
        return newUserLists -> {
            this.userLists.clear();
            this.userLists.addAll(newUserLists);
            if (currentlyDisplayed == USER_LISTS) {
                loadData();
            }
        };
    }

    private Observer<List<Item>> initItemObserver() {
        return items -> {
            this.itemList.clear();
            this.itemList.addAll(items);
            if (currentlyDisplayed == ITEMS) {
                loadData();
            }
        };
    }

    private Observer<String> initUserListErrorObserver() {
        return errorMessage -> {
            if (currentlyDisplayed == USER_LISTS) {
                errorLiveData.setValue(errorMessage);
            }
        };
    }

    private Observer<String> initItemErrorObserver() {
        return errorMessage -> {
            if (currentlyDisplayed == ITEMS) {
                errorLiveData.setValue(errorMessage);
            }
        };
    }


    void userListClicked(int id, String userListTitle) {
        currentlyDisplayed = ITEMS;
        displayLoading.setValue(true);
        changeTitle(userListTitle);
        itemViewModel.getItems(id);
    }


    /**
     * Start DialogFragment from a Fragment for a result
     * https://stackoverflow.com/a/13433770
     */
    public void add() {
        throw new UnsupportedOperationException();
    }

    /**
     * Edit
     */
    void swipedRight(int position) {
        switch (currentlyDisplayed) {
            case USER_LISTS:

                break;
            case ITEMS:

                break;
        }
    }


    /**
     * Delete
     */
    void swipedLeft(int position) {
        switch (currentlyDisplayed) {
            case USER_LISTS:
                userListsAdapter.remove(position);
                temporaryUserList = userLists.get(position);
                temporaryUserListPosition = position;
                break;
            case ITEMS:
                itemsAdapter.remove(position);
                temporaryItem = itemList.get(position);
                temporaryItemPosition = position;
                break;
        }
        eventNotifyUserOfDeletion.call();
    }

    void undoRecentDeletion() {
        switch (currentlyDisplayed) {
            case USER_LISTS:
                userListsAdapter.reAdd(temporaryUserListPosition, temporaryUserList);
                break;
            case ITEMS:
                itemsAdapter.reAdd(temporaryItemPosition, temporaryItem);
                break;
        }
        clearTemporary();
    }

    void deletionNotificationTimedOut() {
        switch (currentlyDisplayed) {
            case USER_LISTS:
                userListViewModel.delete(temporaryUserList.getId());
                break;
            case ITEMS:
                itemViewModel.delete(temporaryItem.getId());
                break;
        }
        clearTemporary();
    }


    void refresh() {
        Timber.i("refresh");
    }


    private void loadData() {
        switch (currentlyDisplayed) {
            case USER_LISTS:
                loadUserListData();
                break;
            case ITEMS:
                loadItemData();
                break;
        }
    }

    private void loadUserListData() {
        if (userLists.isEmpty()) {
            setEmptyListError();
        } else {
            userListsAdapter.swapData(userLists);
            commonLoadDataSteps(userListsAdapter);
        }
    }

    private void loadItemData() {
        if (itemList.isEmpty()) {
            setEmptyListError();
        } else {
            itemsAdapter.swapData(itemList);
            commonLoadDataSteps(itemsAdapter);
        }
    }

    private void commonLoadDataSteps(RecyclerView.Adapter adapter) {
        recyclerViewAdapter.setValue(adapter);
        displayLoading.setValue(false);
    }


    private void changeTitle(@Nullable String title) {
        this.toolbarTitle.setValue(title);
    }

    private void setEmptyListError() {
        errorLiveData.setValue(
                getApplication().getString(R.string.error_msg_empty_list)
        );
    }

    private void clearTemporary() {
        temporaryUserList = null;
        temporaryUserListPosition = -1;

        temporaryItem = null;
        temporaryItemPosition = -1;
    }


    public LiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    LiveData<Boolean> getDisplayLoading() {
        return displayLoading;
    }

    LiveData<RecyclerView.Adapter> getRecyclerViewAdapter() {
        return recyclerViewAdapter;
    }

    LiveData<String> getError() {
        return errorLiveData;
    }

    SingleLiveEvent<Void> getEventNotifyUserOfDeletion() {
        return eventNotifyUserOfDeletion;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        userListViewModel.getUserLists().removeObserver(userListObserver);
        itemViewModel.getItemList().removeObserver(itemObserver);
        userListViewModel.getError().removeObserver(userListErrorObserver);
        itemViewModel.getError().removeObserver(itemErrorObserver);
    }
}
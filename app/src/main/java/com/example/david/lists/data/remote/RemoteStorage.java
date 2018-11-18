package com.example.david.lists.data.remote;

import com.example.david.lists.BuildConfig;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.util.SingleLiveEvent;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import androidx.lifecycle.LiveData;
import timber.log.Timber;

public final class RemoteStorage implements IRemoteStorageContract {

    private final RemoteDao dao;

    // TODO Update when UserList is deleted
    private final SingleLiveEvent<List<UserList>> eventDeleteUserLists;


    private static RemoteStorage instance;

    public static IRemoteStorageContract getInstance() {
        if (instance == null) {
            instance = new RemoteStorage();
        }
        return instance;
    }

    private RemoteStorage() {
        dao = RemoteDao.getInstance(userListsListener(), itemsListener());
        eventDeleteUserLists = new SingleLiveEvent<>();
    }

    private EventListener<QuerySnapshot> userListsListener() {
        return (queryDocumentSnapshots, e) -> {
            if (e != null) {
                onFailure(e);
                return;
            }

            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED:
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        break;
                }
            }
        };
    }

    private EventListener<QuerySnapshot> itemsListener() {
        return (queryDocumentSnapshots, e) -> {
            if (e != null) {
                onFailure(e);
                return;
            }

            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED:
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        break;
                }
            }
        };
    }


    @Override
    public void addUserList(UserList userList) {
        dao.addUserList(userList);
    }

    @Override
    public void addItem(Item item) {
        dao.addItem(item);
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        dao.deleteUserLists(userLists);
    }

    @Override
    public void deleteItems(List<Item> items) {
        dao.deleteItems(items);
    }


    @Override
    public void renameUserList(String userListId, String newName) {
        dao.renameUserList(userListId, newName);
    }

    @Override
    public void renameItem(String itemId, String newName) {
        dao.renameItem(itemId, newName);
    }


    @Override
    public void updateUserListPositionsDecrement(UserList userList, int oldPosition, int newPosition) {
        dao.updateUserListPositionsDecrement(userList, oldPosition, newPosition);
    }

    @Override
    public void updateUserListPositionsIncrement(UserList userList, int oldPosition, int newPosition) {
        dao.updateUserListPositionsIncrement(userList, oldPosition, newPosition);
    }

    @Override
    public void updateItemPositionsDecrement(Item item, int oldPosition, int newPosition) {
        dao.updateItemPositionsDecrement(item, oldPosition, newPosition);
    }


    @Override
    public void updateItemPositionsIncrement(Item item, int oldPosition, int newPosition) {
        dao.updateItemPositionsIncrement(item, oldPosition, newPosition);
    }


    @Override
    public LiveData<List<UserList>> getEventUserListDeleted() {
        return eventDeleteUserLists;
    }


    private void onFailure(Exception exception) {
        if (BuildConfig.DEBUG) {
            Timber.e(exception);
        }
    }
}

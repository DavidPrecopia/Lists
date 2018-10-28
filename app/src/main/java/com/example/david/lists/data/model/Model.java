package com.example.david.lists.data.model;

import android.app.Application;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.local.ILocalStorageContract;
import com.example.david.lists.data.local.LocalStorage;
import com.example.david.lists.data.remote.IRemoteStorageContract;
import com.example.david.lists.data.remote.RemoteStorage;

import java.util.List;

import io.reactivex.Flowable;

public final class Model implements IModelContract {

    private final ILocalStorageContract local;
    private final IRemoteStorageContract remote;

    private static final String TYPE_USER_LIST = "type_user_list";
    private static final String TYPE_ITEM = "type_item";

    private static volatile Model instance;

    public static IModelContract getInstance(Application application) {
        if (instance == null) {
            synchronized (Model.class) {
                instance = new Model(application);
            }
        }
        return instance;
    }

    private Model(Application application) {
        local = LocalStorage.getInstance(application);
        remote = RemoteStorage.getInstance();
    }


    @Override
    public Flowable<List<UserList>> getAllLists() {
        return local.getAllUserLists();
    }

    @Override
    public Flowable<List<Item>> getUserListItems(String userListId) {
        return local.getAllItems(userListId);
    }


    @Override
    public void addUserList(UserList userList) {
        String id = remote.addUserList(userList);
        userList.setId(id);
        local.addUserList(userList);

    }

    @Override
    public void addItem(Item item) {
        String id = remote.addItem(item);
        item.setId(id);
        local.addItem(item);
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        local.deleteUserLists(userLists);
        remote.deleteUserLists(userLists);
    }

    @Override
    public void deleteItems(List<Item> items) {
        local.deleteItems(items);
        remote.deleteItems(items);
    }


    @Override
    public void renameUserList(String userListId, String newTitle) {
        local.renameUserList(userListId, newTitle);
        remote.renameUserList(userListId, newTitle);
    }

    @Override
    public void renameItem(String itemId, String newTitle) {
        local.renameItem(itemId, newTitle);
        remote.renameItem(itemId, newTitle);
    }


    @Override
    public void updateUserListPosition(String userListId, int oldPosition, int newPosition) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return;
        }
        processPositionChange(TYPE_USER_LIST, userListId, oldPosition, newPosition);
    }

    @Override
    public void updateItemPosition(String itemId, int oldPosition, int newPosition) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return;
        }
        processPositionChange(TYPE_ITEM, itemId, oldPosition, newPosition);
    }

    private boolean positionNotChanged(int oldPosition, int newPosition) {
        return oldPosition == newPosition;
    }

    /**
     * Using the same method for both types in order to keep the logic DRY.
     * <em>Incrementing/decrementing oldPosition</em>
     * so the moved row is excluded from the update operation.
     */
    private void processPositionChange(String type, String id, int oldPosition, int newPosition) {
        if (newPosition > oldPosition) {
            decrementPosition(type, id, (oldPosition + 1), newPosition);
        } else if (newPosition < oldPosition) {
            incrementPosition(type, id, (oldPosition - 1), newPosition);
        }
    }

    private void decrementPosition(String type, String id, int oldPosition, int newPosition) {
        switch (type) {
            case TYPE_USER_LIST:
                local.updateUserListPositionsDecrement(id, oldPosition, newPosition);
                remote.updateUserListPositionsDecrement(id, oldPosition, newPosition);
                break;
            case TYPE_ITEM:
                local.updateItemPositionsDecrement(id, oldPosition, newPosition);
                remote.updateItemPositionsDecrement(id, oldPosition, newPosition);
                break;
        }
    }

    private void incrementPosition(String type, String id, int oldPosition, int newPosition) {
        switch (type) {
            case TYPE_USER_LIST:
                local.updateUserListPositionsIncrement(id, oldPosition, newPosition);
                remote.updateUserListPositionsIncrement(id, oldPosition, newPosition);
                break;
            case TYPE_ITEM:
                local.updateItemPositionsIncrement(id, oldPosition, newPosition);
                remote.updateItemPositionsIncrement(id, oldPosition, newPosition);
                break;
        }
    }


    @Override
    public void forceRefreshUserLists() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forceRefreshItems(String userListId) {
        throw new UnsupportedOperationException();
    }
}

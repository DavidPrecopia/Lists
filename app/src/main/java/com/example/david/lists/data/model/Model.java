package com.example.david.lists.data.model;

import android.app.Application;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.local.ILocalStorageContract;
import com.example.david.lists.data.local.LocalStorage;
import com.example.david.lists.data.remote.IRemoteStorageContract;
import com.example.david.lists.data.remote.RemoteStorage;

import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;

public final class Model implements IModelContract {

    private final ILocalStorageContract local;
    private final IRemoteStorageContract remote;

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
        remote = RemoteStorage.getInstance(application);
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
        validateObject(userList);
        String id = remote.addUserList(userList);
        local.addUserList(
                Collections.singletonList(new UserList(id, userList))
        );
    }

    @Override
    public void addItem(Item item) {
        validateObject(item);
        String id = remote.addItem(item);
        local.addItems(
                Collections.singletonList(new Item(id, item))
        );
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        validateList(userLists);
        local.deleteUserLists(userLists);
        remote.deleteUserLists(userLists);
    }

    @Override
    public void deleteItems(List<Item> items) {
        validateList(items);
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
    public void updateUserListPosition(UserList userList, int oldPosition, int newPosition) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return;
        } else {
            validateObject(userList);
            validatePositions(oldPosition, newPosition);
        }

        processUserListPositionChange(userList, oldPosition, newPosition);
    }

    private void processUserListPositionChange(UserList userList, int oldPosition, int newPosition) {
        if (shouldDecrement(oldPosition, newPosition)) {
            local.updateUserListPositionsDecrement(userList, oldPosition, newPosition);
            remote.updateUserListPositionsDecrement(userList, oldPosition, newPosition);
        } else if (shouldIncrement(oldPosition, newPosition)) {
            local.updateUserListPositionsIncrement(userList, oldPosition, newPosition);
            remote.updateUserListPositionsIncrement(userList, oldPosition, newPosition);
        }
    }

    @Override
    public void updateItemPosition(Item item, int oldPosition, int newPosition) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return;
        } else {
            validateObject(item);
            validatePositions(oldPosition, newPosition);
        }

        processItemPositionChange(item, oldPosition, newPosition);
    }

    private void processItemPositionChange(Item item, int oldPosition, int newPosition) {
        if (shouldDecrement(oldPosition, newPosition)) {
            local.updateItemPositionsDecrement(item, oldPosition, newPosition);
            remote.updateItemPositionsDecrement(item, oldPosition, newPosition);
        } else if (shouldIncrement(oldPosition, newPosition)) {
            local.updateItemPositionsIncrement(item, oldPosition, newPosition);
            remote.updateItemPositionsIncrement(item, oldPosition, newPosition);
        }
    }

    private boolean positionNotChanged(int oldPosition, int newPosition) {
        return oldPosition == newPosition;
    }

    private boolean shouldIncrement(int oldPosition, int newPosition) {
        return newPosition < oldPosition;
    }

    private boolean shouldDecrement(int oldPosition, int newPosition) {
        return newPosition > oldPosition;
    }


    private void validateObject(Object object) {
        if (object == null) {
            nullObjectException();
        }
    }

    private void validateList(List list) {
        if (list == null) {
            nullObjectException();
        } else if (list.isEmpty()) {
            throw new IllegalArgumentException("Passed List is empty");
        }
    }

    private void validatePositions(int positionOne, int positionTwo) {
        if (positionOne < 0 || positionTwo < 0) {
            throw new IllegalArgumentException("One or both positions are less then 0");
        }
    }

    private void nullObjectException() {
        throw new IllegalArgumentException("Parameter cannot be null");
    }
}

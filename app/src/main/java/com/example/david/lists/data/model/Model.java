package com.example.david.lists.data.model;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.remote.IRemoteStorageContract;
import com.example.david.lists.data.remote.RemoteStorage;

import java.util.Collections;
import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.Flowable;

public final class Model implements IModelContract {

    private final IRemoteStorageContract remote;


    private static volatile Model instance;

    public static IModelContract getInstance() {
        if (instance == null) {
            synchronized (Model.class) {
                instance = new Model();
            }
        }
        return instance;
    }

    private Model() {
        remote = RemoteStorage.getInstance();
    }


    @Override
    public Flowable<List<UserList>> getAllLists() {
        return null;
    }

    // TODO Initialize a Snapshot listener for Items
    @Override
    public Flowable<List<Item>> getUserListItems(String userListId) {
        return null;
    }


    @Override
    public void addUserList(UserList userList) {
        validateObject(userList);
        remote.addUserList(userList);
    }

    @Override
    public void addItem(Item item) {
        validateObject(item);
        remote.addItem(item);
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        validateList(userLists);
        remote.deleteUserLists(userLists);
    }

    @Override
    public void deleteItems(List<Item> items) {
        validateList(items);
        remote.deleteItems(items);
    }


    @Override
    public void renameUserList(String userListId, String newTitle) {
        remote.renameUserList(userListId, newTitle);
    }

    @Override
    public void renameItem(String itemId, String newTitle) {
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
            remote.updateUserListPositionsDecrement(userList, oldPosition, newPosition);
        } else if (shouldIncrement(oldPosition, newPosition)) {
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
            remote.updateItemPositionsDecrement(item, oldPosition, newPosition);
        } else if (shouldIncrement(oldPosition, newPosition)) {
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


    @Override
    public LiveData<List<UserList>> getEventUserListDeleted() {
        return remote.getEventUserListDeleted();
    }


    private void nullObjectException() {
        throw new IllegalArgumentException("Parameter cannot be null");
    }
}

package com.example.david.lists.data.model;

import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.remote.IRemoteStorageContract;
import com.example.david.lists.data.remote.RemoteStorage;
import com.example.david.lists.util.UtilExceptions;

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
    public Flowable<List<Group>> getAllGroups() {
        return remote.getGroups();
    }

    @Override
    public Flowable<List<Item>> getGroupItems(String groupId) {
        return remote.getItems(groupId);
    }


    @Override
    public void addGroup(Group group) {
        validateObject(group);
        remote.addGroup(group);
    }

    @Override
    public void addItem(Item item) {
        validateObject(item);
        remote.addItem(item);
    }


    @Override
    public void deleteGroups(List<Group> groups) {
        validateList(groups);
        remote.deleteGroups(groups);
    }

    @Override
    public void deleteItems(List<Item> items) {
        validateList(items);
        remote.deleteItems(items);
    }


    @Override
    public void renameGroup(String groupId, String newTitle) {
        remote.renameGroup(groupId, newTitle);
    }

    @Override
    public void renameItem(String itemId, String newTitle) {
        remote.renameItem(itemId, newTitle);
    }


    @Override
    public void updateGroupPosition(Group group, int oldPosition, int newPosition) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return;
        } else {
            validateObject(group);
            validatePositions(oldPosition, newPosition);
        }

        remote.updateGroupPosition(group, oldPosition, newPosition);
    }

    @Override
    public void updateItemPosition(Item item, int oldPosition, int newPosition) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return;
        } else {
            validateObject(item);
            validatePositions(oldPosition, newPosition);
        }

        remote.updateItemPosition(item, oldPosition, newPosition);
    }

    private boolean positionNotChanged(int oldPosition, int newPosition) {
        return oldPosition == newPosition;
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
            UtilExceptions.throwException(new IllegalArgumentException("Passed List is empty"));
        }
    }

    private void validatePositions(int positionOne, int positionTwo) {
        if (positionOne < 0 || positionTwo < 0) {
            UtilExceptions.throwException(new IllegalArgumentException("One or both positions are less then 0"));
        }
    }


    @Override
    public LiveData<List<Group>> getEventGroupDeleted() {
        return remote.getEventGroupDeleted();
    }


    private void nullObjectException() {
        UtilExceptions.throwException(new IllegalArgumentException("Parameter cannot be null"));
    }
}

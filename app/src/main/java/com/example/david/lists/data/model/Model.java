package com.example.david.lists.data.model;

import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.remote.IRemoteStorageContract;
import com.example.david.lists.data.remote.RemoteStorage;

import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

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
        completableUtil(Completable.fromAction(() -> remote.addGroup(group)));
    }

    @Override
    public void addItem(Item item) {
        validateObject(item);
        completableUtil(Completable.fromAction(() -> remote.addItem(item)));
    }


    @Override
    public void deleteGroups(List<Group> groups) {
        validateList(groups);
        completableUtil(Completable.fromAction(() -> remote.deleteGroups(groups)));
    }

    @Override
    public void deleteItems(List<Item> items) {
        validateList(items);
        completableUtil(Completable.fromAction(() -> remote.deleteItems(items)));
    }


    @Override
    public void renameGroup(String groupId, String newTitle) {
        completableUtil(Completable.fromAction(() -> remote.renameGroup(groupId, newTitle)));
    }

    @Override
    public void renameItem(String itemId, String newTitle) {
        completableUtil(Completable.fromAction(() -> remote.renameItem(itemId, newTitle)));
    }


    @Override
    public void updateGroupPosition(Group group, int oldPosition, int newPosition) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return;
        } else {
            validateObject(group);
            validatePositions(oldPosition, newPosition);
        }

        processGroupPositionChange(group, oldPosition, newPosition);
    }

    private void processGroupPositionChange(Group group, int oldPosition, int newPosition) {
        if (shouldDecrement(oldPosition, newPosition)) {
            completableUtil(Completable.fromAction(() ->
                    remote.updateGroupPositionsDecrement(group, oldPosition, newPosition)
            ));
        } else if (shouldIncrement(oldPosition, newPosition)) {
            completableUtil(Completable.fromAction(() ->
                    remote.updateGroupPositionsIncrement(group, oldPosition, newPosition)
            ));
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
            completableUtil(Completable.fromAction(() ->
                    remote.updateItemPositionsDecrement(item, oldPosition, newPosition)
            ));
        } else if (shouldIncrement(oldPosition, newPosition)) {
            completableUtil(Completable.fromAction(() ->
                    remote.updateItemPositionsIncrement(item, oldPosition, newPosition)
            ));
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
    public LiveData<List<Group>> getEventGroupDeleted() {
        return remote.getEventGroupDeleted();
    }


    private void completableUtil(Completable completable) {
        completable.subscribeOn(Schedulers.io()).subscribe();
    }

    private void nullObjectException() {
        throw new IllegalArgumentException("Parameter cannot be null");
    }
}

package com.example.david.lists.data.model;

import android.app.Application;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.local.LocalDao;
import com.example.david.lists.data.local.LocalDatabase;
import com.example.david.lists.data.remote.IRemoteDatabaseContract;
import com.example.david.lists.data.remote.RemoteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public final class Model implements IModelContract {

    private final LocalDao local;
    private final IRemoteDatabaseContract remote;

    private static final String TYPE_USER_LIST = "type_user_list";
    private static final String TYPE_ITEM = "type_item";

    private static volatile Model instance;

    public static Model getInstance(Application application) {
        if (instance == null) {
            synchronized (Model.class) {
                instance = new Model(application);
            }
        }
        return instance;
    }

    private Model(Application application) {
        local = LocalDatabase.getInstance(application).getLocalDao();
        remote = RemoteDatabase.getInstance();
    }


    @Override
    public Flowable<List<UserList>> getAllLists() {
        return local.getAllUserLists();
    }

    @Override
    public Flowable<List<Item>> getUserListItems(int userListId) {
        return local.getAllItems(userListId);
    }


    @Override
    public void addUserList(UserList userList) {
        long id = local.addUserList(userList);

        userList.setId(longToInt(id));
        remote.addUserList(userList);
    }

    @Override
    public void addItem(Item item) {
        long id = local.addItem(item);

        item.setId(longToInt(id));
        remote.addItem(item);
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        local.deleteList(getUserListsIds(userLists));
        remote.deleteUserLists(userLists);
    }

    private List<Integer> getUserListsIds(List<UserList> userLists) {
        List<Integer> userListsIds = new ArrayList<>();
        for (UserList userList : userLists) {
            userListsIds.add(userList.getId());
        }
        return userListsIds;
    }

    @Override
    public void deleteItems(List<Item> items) {
        local.deleteItem(getItemIds(items));
        remote.deleteItems(items);
    }

    private List<Integer> getItemIds(List<Item> items) {
        List<Integer> itemIds = new ArrayList<>();
        for (Item item : items) {
            itemIds.add(item.getId());
        }
        return itemIds;
    }


    @Override
    public void renameUserList(int userListId, String newTitle) {
        local.renameUserList(userListId, newTitle);
        remote.renameUserList(userListId, newTitle);
    }

    @Override
    public void renameItem(int itemId, String newTitle) {
        local.renameItem(itemId, newTitle);
        remote.renameItem(itemId, newTitle);
    }


    @Override
    public void updateUserListPosition(int userListId, int oldPosition, int newPosition) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return;
        }
        processPositionChange(TYPE_USER_LIST, userListId, oldPosition, newPosition);
    }

    @Override
    public void updateItemPosition(int itemId, int oldPosition, int newPosition) {
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
    private void processPositionChange(String type, int id, int oldPosition, int newPosition) {
        if (newPosition > oldPosition) {
            decrementPosition(type, id, (oldPosition + 1), newPosition);
        } else if (newPosition < oldPosition) {
            incrementPosition(type, id, (oldPosition - 1), newPosition);
        }
        updateLocalPosition(type, id, newPosition);
    }

    private void decrementPosition(String type,int id, int oldPosition, int newPosition) {
        switch (type) {
            case TYPE_USER_LIST:
                local.updateUserListPositionsDecrement(oldPosition, newPosition);
                remote.updateUserListPositionsDecrement(id, oldPosition, newPosition);
                break;
            case TYPE_ITEM:
                local.updateItemPositionsDecrement(oldPosition, newPosition);
                remote.updateItemPositionsDecrement(id, oldPosition, newPosition);
                break;
        }
    }

    private void incrementPosition(String type, int id, int oldPosition, int newPosition) {
        switch (type) {
            case TYPE_USER_LIST:
                local.updateUserListPositionsIncrement(oldPosition, newPosition);
                remote.updateUserListPositionsIncrement(id, oldPosition, newPosition);
                break;
            case TYPE_ITEM:
                local.updateItemPositionsIncrement(oldPosition, newPosition);
                remote.updateItemPositionsIncrement(id, oldPosition, newPosition);
                break;
        }
    }

    private void updateLocalPosition(String type, int id, int newPosition) {
        switch (type) {
            case TYPE_USER_LIST:
                local.updateUserListPosition(id, newPosition);
                break;
            case TYPE_ITEM:
                local.updateItemPosition(id, newPosition);
                break;
        }
    }


    @Override
    public void forceRefreshUserLists() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forceRefreshItems(int userListId) {
        throw new UnsupportedOperationException();
    }


    private int longToInt(long id) {
        return (int) id;
    }
}

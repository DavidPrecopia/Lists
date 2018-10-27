package com.example.david.lists.data.local;

import android.app.Application;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public final class LocalStorage implements ILocalStorageContract {

    private final LocalDao dao;

    private static volatile LocalStorage instance;

    public static ILocalStorageContract getInstance(Application application) {
        if (instance == null) {
            instance = new LocalStorage(application);
        }
        return instance;
    }

    private LocalStorage(Application application) {
        dao = LocalDatabase.getInstance(application).getLocalDao();
    }


    @Override
    public Flowable<List<UserList>> getAllUserLists() {
        return dao.getAllUserLists();
    }

    @Override
    public Flowable<List<Item>> getAllItems(int userListId) {
        return dao.getAllItems(userListId);
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
    public void deleteUserLists(List<UserList> userList) {
        dao.deleteUserList(getUserListsIds(userList));
    }

    private List<Integer> getUserListsIds(List<UserList> userLists) {
        List<Integer> userListsIds = new ArrayList<>();
        for (UserList userList : userLists) {
            userListsIds.add(userList.getId());
        }
        return userListsIds;
    }

    @Override
    public void deleteItems(List<Item> item) {
        dao.deleteItem(getItemIds(item));
    }

    private List<Integer> getItemIds(List<Item> items) {
        List<Integer> itemIds = new ArrayList<>();
        for (Item item : items) {
            itemIds.add(item.getId());
        }
        return itemIds;
    }


    @Override
    public void renameUserList(int userListId, String newName) {
        dao.renameUserList(userListId, newName);
    }

    @Override
    public void renameItem(int itemId, String newName) {
        dao.renameItem(itemId, newName);
    }


    @Override
    public void updateUserListPositionsIncrement(int userListId, int oldPosition, int newPosition) {
        dao.updateUserListPositionsIncrement(oldPosition, newPosition);
        updateUserListPosition(userListId, newPosition);
    }

    @Override
    public void updateUserListPositionsDecrement(int userListId, int oldPosition, int newPosition) {
        dao.updateUserListPositionsDecrement(oldPosition, newPosition);
        updateUserListPosition(userListId, newPosition);
    }

    private void updateUserListPosition(int userListId, int newPosition) {
        dao.updateUserListPosition(userListId, newPosition);
    }

    @Override
    public void updateItemPositionsIncrement(int itemId, int oldPosition, int newPosition) {
        dao.updateItemPositionsIncrement(oldPosition, newPosition);
        updateItemPosition(itemId, newPosition);
    }

    @Override
    public void updateItemPositionsDecrement(int itemId, int oldPosition, int newPosition) {
        dao.updateItemPositionsDecrement(oldPosition, newPosition);
        updateItemPosition(itemId, newPosition);
    }

    private void updateItemPosition(int itemId, int newPosition) {
        dao.updateItemPosition(itemId, newPosition);
    }
}

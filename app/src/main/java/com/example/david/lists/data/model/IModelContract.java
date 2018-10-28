package com.example.david.lists.data.model;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface IModelContract {
    Flowable<List<UserList>> getAllLists();

    Flowable<List<Item>> getUserListItems(String userListId);

    void addUserList(UserList userList);

    void addItem(Item item);

    void deleteUserLists(List<UserList> userLists);

    void deleteItems(List<Item> items);

    void renameUserList(String userListId, String newName);

    void renameItem(String itemId, String newName);

    void updateUserListPosition(UserList userList, int oldPosition, int newPosition);

    void updateItemPosition(Item item, int oldPosition, int newPosition);

    void forceRefreshUserLists();

    void forceRefreshItems(String userListId);
}

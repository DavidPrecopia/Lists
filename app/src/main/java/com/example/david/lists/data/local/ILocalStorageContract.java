package com.example.david.lists.data.local;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface ILocalStorageContract {
    Flowable<List<UserList>> getAllUserLists();

    Flowable<List<Item>> getAllItems(String userListId);

    void addUserList(UserList userList);

    void addItem(Item item);

    void deleteUserLists(List<UserList> userList);

    void deleteItems(List<Item> item);

    void renameUserList(String userListId, String newName);

    void renameItem(String itemId, String newName);

    void updateUserListPositionsIncrement(String userListId, int oldPosition, int newPosition);

    void updateUserListPositionsDecrement(String userListId, int oldPosition, int newPosition);

    void updateItemPositionsIncrement(String itemId, int oldPosition, int newPosition);

    void updateItemPositionsDecrement(String itemId, int oldPosition, int newPosition);
}

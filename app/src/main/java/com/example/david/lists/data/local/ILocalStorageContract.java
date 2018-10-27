package com.example.david.lists.data.local;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface ILocalStorageContract {
    Flowable<List<UserList>> getAllUserLists();

    Flowable<List<Item>> getAllItems(int userListId);

    void addUserList(UserList userList);

    void addItem(Item item);

    void deleteUserLists(List<UserList> userList);

    void deleteItems(List<Item> item);

    void renameUserList(int userListId, String newName);

    void renameItem(int itemId, String newName);

    void updateUserListPositionsIncrement(int userListId, int oldPosition, int newPosition);

    void updateUserListPositionsDecrement(int userListId, int oldPosition, int newPosition);

    void updateItemPositionsIncrement(int itemId, int oldPosition, int newPosition);

    void updateItemPositionsDecrement(int itemId, int oldPosition, int newPosition);
}

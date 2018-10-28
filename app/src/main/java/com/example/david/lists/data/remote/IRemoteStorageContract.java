package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IRemoteStorageContract {
    String addUserList(UserList userList);

    String addItem(Item item);

    void deleteUserLists(List<UserList> userList);

    void deleteItems(List<Item> item);

    void renameUserList(String userListId, String newName);

    void renameItem(String itemId, String newName);

    void updateUserListPositionsIncrement(String userListId, int oldPosition, int newPosition);

    void updateUserListPositionsDecrement(String userListId, int oldPosition, int newPosition);

    void updateItemPositionsIncrement(String itemId, int oldPosition, int newPosition);

    void updateItemPositionsDecrement(String itemId, int oldPosition, int newPosition);
}

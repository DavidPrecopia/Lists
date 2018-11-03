package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IRemoteStorageContract {
    UserList addUserList(UserList userList);

    Item addItem(Item item);

    void deleteUserLists(List<UserList> userList);

    void deleteItems(List<Item> item);

    void renameUserList(String userListId, String newName);

    void renameItem(String itemId, String newName);

    void updateUserListPositionsIncrement(UserList userList, int oldPosition, int newPosition);

    void updateUserListPositionsDecrement(UserList userList, int oldPosition, int newPosition);

    void updateItemPositionsIncrement(Item item, int oldPosition, int newPosition);

    void updateItemPositionsDecrement(Item item, int oldPosition, int newPosition);
}

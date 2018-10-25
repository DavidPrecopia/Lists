package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IRemoteDatabaseContract {
    void addUserList(UserList userList);

    void addItem(Item item);

    void deleteUserLists(List<UserList> userList);

    void deleteItems(List<Item> item);
}

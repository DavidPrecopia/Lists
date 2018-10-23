package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

public interface IRemoteDatabaseContract {
    void addUserList(UserList userList);

    void addItem(Item item);
}

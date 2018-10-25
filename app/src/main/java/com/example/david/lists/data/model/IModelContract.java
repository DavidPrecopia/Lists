package com.example.david.lists.data.model;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface IModelContract {
    Flowable<List<UserList>> getAllLists();

    Flowable<List<Item>> getUserListItems(int listId);

    void addUserList(UserList userList);

    void addItem(Item item);

    void deleteUserLists(List<UserList> userLists);

    void deleteItems(List<Item> items);

    void changeUserListTitle(int userListId, String newName);

    void changeItemTitle(int itemId, String newName);

    void moveUserListPosition(int userListId, int oldPosition, int newPosition);

    void moveItemPosition(int itemId, int oldPosition, int newPosition);

    void forceRefreshUserLists();

    void forceRefreshItems(int userListId);
}

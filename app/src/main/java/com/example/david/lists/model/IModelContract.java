package com.example.david.lists.model;

import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface IModelContract {
    Flowable<List<UserList>> getAllLists();

    Flowable<List<Item>> getUserListItems(int listId);

    void addUserList(UserList userList);

    void addItem(Item item);

    void deleteUserList(List<Integer> userListId);

    void deleteItem(List<Integer> itemId);

    void changeUserListTitle(int userListId, String newName);

    void changeItemTitle(int itemId, String newName);

    void moveUserListPosition(int userListId, int oldPosition, int newPosition);

    void moveItemPosition(int itemId, int oldPosition, int newPosition);

    void forceRefreshUserLists();

    void forceRefreshItems(int userListId);
}

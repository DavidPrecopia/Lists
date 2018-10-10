package com.example.david.lists.model;

import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface IModelContract {
    Flowable<List<UserList>> getAllLists();

    Flowable<List<Item>> getUserListItems(int listId);

    void addList(UserList list);

    void addItem(Item item);

    void deleteList(int listId);

    void deleteItem(int itemId);

    void changeListTitle(int listId, String newName);

    void changeItemTitle(int itemId, String newName);

    void moveUserListPosition(int listId, int oldPosition, int newPosition);

    void moveItemPosition(int itemId, int oldPosition, int newPosition);

    void forceRefreshUserLists();

    void forceRefreshItems(int listId);
}

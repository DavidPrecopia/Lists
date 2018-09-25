package com.example.david.lists.model;

import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface IModelContract {
    Flowable<List<UserList>> getAllLists();

    Flowable<List<Item>> getListContents(int listId);

    void addList(UserList list);

    void addItem(Item item);

    void deleteList(int listId);

    void deleteItem(int itemId);

    void changeListName(int listId, String newName);

    void changeItemName(int itemId, String newName);

    void moveListPosition(int listId, int newPosition);

    void moveItemPosition(int itemId, int newPosition);

    void forceRefreshLists();

    void forceRefreshListContents(int listId);
}

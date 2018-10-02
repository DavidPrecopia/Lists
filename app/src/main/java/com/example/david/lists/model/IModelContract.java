package com.example.david.lists.model;

import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface IModelContract {
    Flowable<List<UserList>> getAllLists();

    Flowable<List<Item>> getListItems(int listId);

    void addList(UserList list);

    void addItem(Item item);

    void deleteList(int listId);

    void deleteItem(int itemId);

    void changeListTitle(int listId, String newName);

    void changeItemTitle(int itemId, String newName);

    void moveListPosition(int listId, int newPosition);

    void moveItemPosition(int itemId, int newPosition);

    void forceRefreshLists();

    void forceRefreshListContents(int listId);
}

package com.example.david.lists.data.repository;

import androidx.lifecycle.LiveData;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface IRepository {
    Flowable<List<UserList>> getAllUserLists();

    Flowable<List<Item>> getItems(String userListId);

    void addUserList(UserList userList);

    void addItem(Item item);

    void deleteUserLists(List<UserList> userListList);

    void deleteItems(List<Item> itemList);

    void renameUserList(String userListId, String newTitle);

    void renameItem(String itemId, String newTitle);

    void updateUserListPosition(UserList userList, int oldPosition, int newPosition);

    void updateItemPosition(Item item, int oldPosition, int newPosition);

    LiveData<List<UserList>> getEventUserListDeleted();
}

package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public interface IRemoteRepositoryContract {
    interface Repository {
        Flowable<List<UserList>> getUserLists();

        Flowable<List<Item>> getItems(String userListId);

        void addUserList(UserList userList);

        void addItem(Item item);

        void deleteUserLists(List<UserList> userListList);

        void deleteItems(List<Item> itemList);

        void renameUserList(String userListId, String newName);

        void renameItem(String itemId, String newName);

        void updateUserListPosition(UserList userList, int oldPosition, int newPosition);

        void updateItemPosition(Item item, int oldPosition, int newPosition);

        Flowable<List<UserList>> getEventUserListDeleted();
    }

    interface SnapshotListener {
        Flowable<List<UserList>> getUserListFlowable();

        Flowable<List<Item>> getItemFlowable(String userListId);

        Flowable<List<UserList>> getDeletedUserLists();
    }
}

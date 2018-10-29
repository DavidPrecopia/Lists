package com.example.david.lists.data.local;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Flowable;

import static androidx.room.OnConflictStrategy.REPLACE;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ITEM_USER_LIST_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.local.LocalDatabaseConstants.ITEM_TABLE_NAME;
import static com.example.david.lists.data.local.LocalDatabaseConstants.USER_LIST_TABLE_NAME;

@Dao
abstract class LocalDao {
    @Query("SELECT * FROM " + USER_LIST_TABLE_NAME
            + " ORDER BY " + FIELD_POSITION)
    abstract Flowable<List<UserList>> getAllUserLists();

    @Query("SELECT * FROM " + ITEM_TABLE_NAME
            + " WHERE " + FIELD_ITEM_USER_LIST_ID + " = :userListId"
            + " ORDER BY " + FIELD_POSITION)
    abstract Flowable<List<Item>> getAllItems(String userListId);


    @Insert(onConflict = REPLACE)
    abstract void addUserList(UserList list);

    @Insert(onConflict = REPLACE)
    abstract void addItem(Item item);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + FIELD_TITLE + " = :newTitle"
            + " WHERE " + FIELD_ID + " = :userListId")
    abstract void renameUserList(String userListId, String newTitle);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + FIELD_TITLE + " = :newTitle"
            + " WHERE " + FIELD_ID + " = :itemId")
    abstract void renameItem(String itemId, String newTitle);


    @Query("DELETE FROM " + USER_LIST_TABLE_NAME + " WHERE " + FIELD_ID + " IN (:userListIds)")
    abstract void deleteUserList(List<String> userListIds);

    @Query("DELETE FROM " + ITEM_TABLE_NAME + " WHERE " + FIELD_ID + " IN (:itemIds)")
    abstract void deleteItem(List<String> itemIds);


    @Transaction
    void updateUserListPositionsIncrementTransaction(String userListId, int oldPosition, int newPosition) {
        incrementUserListPosition(oldPosition, newPosition);
        updateUserListPosition(userListId, newPosition);
    }

    @Transaction
    void updateUserListPositionsDecrementTransaction(String userListId, int oldPosition, int newPosition) {
        decrementUserListPosition(oldPosition, newPosition);
        updateUserListPosition(userListId, newPosition);
    }

    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + FIELD_POSITION + " = " + FIELD_POSITION + " + 1"
            + " WHERE " + FIELD_POSITION + " BETWEEN :newPosition AND :oldPosition")
    abstract void incrementUserListPosition(int oldPosition, int newPosition);

    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + FIELD_POSITION + " = " + FIELD_POSITION + " - 1"
            + " WHERE " + FIELD_POSITION + " BETWEEN :oldPosition AND :newPosition")
    abstract void decrementUserListPosition(int oldPosition, int newPosition);

    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + FIELD_POSITION + " = :newPosition"
            + " WHERE " + FIELD_ID + " = :userListId")
    abstract void updateUserListPosition(String userListId, int newPosition);


    @Transaction
    void updateItemPositionsIncrementTransaction(String itemId, int oldPosition, int newPosition) {
        incrementItemPosition(oldPosition, newPosition);
        updateItemPosition(itemId, newPosition);
    }

    @Transaction
    void updateItemPositionsDecrementTransaction(String itemId, int oldPosition, int newPosition) {
        decrementItemPosition(oldPosition, newPosition);
        updateItemPosition(itemId, newPosition);
    }

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + FIELD_POSITION + " = " + FIELD_POSITION + " + 1"
            + " WHERE " + FIELD_POSITION + " BETWEEN :oldPosition AND :newPosition")
    abstract void incrementItemPosition(int oldPosition, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + FIELD_POSITION + " = " + FIELD_POSITION + " - 1"
            + " WHERE " + FIELD_POSITION + " BETWEEN :oldPosition AND :newPosition")
    abstract void decrementItemPosition(int oldPosition, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + FIELD_POSITION + " = :newPosition"
            + " WHERE " + FIELD_ID + " = :itemId")
    abstract void updateItemPosition(String itemId, int newPosition);
}

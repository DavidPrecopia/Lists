package com.example.david.lists.database;

import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Flowable;

import static androidx.room.OnConflictStrategy.REPLACE;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_ID;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_LIST_ID;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_NAME;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_POSITION;
import static com.example.david.lists.database.DatabaseContract.ITEM_TABLE_NAME;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_ID;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_NAME;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_POSITION;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_TABLE_NAME;

@Dao
public interface LocalDao {
    @Query("SELECT * FROM " + USER_LIST_TABLE_NAME
            + " ORDER BY " + USER_LIST_COLUMN_POSITION)
    Flowable<List<UserList>> getAllLists();

    @Query("SELECT * FROM " + ITEM_TABLE_NAME
            + " WHERE " + ITEM_COLUMN_LIST_ID + " = :listId"
            + " ORDER BY " + ITEM_COLUMN_POSITION)
    Flowable<List<Item>> getListItems(int listId);


    @Insert(onConflict = REPLACE)
    void addUserList(UserList list);

    @Insert(onConflict = REPLACE)
    void addItem(Item item);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + USER_LIST_COLUMN_NAME + " = :newTitle"
            + " WHERE " + USER_LIST_COLUMN_ID + " = :listId")
    void changeListTitle(int listId, String newTitle);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + ITEM_COLUMN_NAME + " = :newTitle"
            + " WHERE " + ITEM_COLUMN_ID + " = :itemId")
    void changeItemTitle(int itemId, String newTitle);


    @Query("DELETE FROM " + USER_LIST_TABLE_NAME + " WHERE " + USER_LIST_COLUMN_ID + " IN (:listIds)")
    void deleteList(List<Integer> listIds);

    @Query("DELETE FROM " + ITEM_TABLE_NAME + " WHERE " + ITEM_COLUMN_ID + " IN (:itemIds)")
    void deleteItem(List<Integer> itemIds);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + USER_LIST_COLUMN_POSITION + " = :newPosition"
            + " WHERE " + USER_LIST_COLUMN_ID + " = :listId")
    void moveListPosition(int listId, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + ITEM_COLUMN_POSITION + " = :newPosition"
            + " WHERE " + ITEM_COLUMN_ID + " = :itemId")
    void moveItemPosition(int itemId, int newPosition);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + USER_LIST_COLUMN_POSITION + " = " + USER_LIST_COLUMN_POSITION + " - 1"
            + " WHERE " + USER_LIST_COLUMN_POSITION + " BETWEEN :oldPosition AND :newPosition")
    void updateUserListPositionsDecrement(int oldPosition, int newPosition);

    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + USER_LIST_COLUMN_POSITION + " = " + USER_LIST_COLUMN_POSITION + " + 1"
            + " WHERE " + USER_LIST_COLUMN_POSITION + " BETWEEN :newPosition AND :oldPosition")
    void updateUserListPositionsIncrement(int oldPosition, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + ITEM_COLUMN_POSITION + " = " + ITEM_COLUMN_POSITION + " + 1"
            + " WHERE " + ITEM_COLUMN_POSITION + " BETWEEN :oldPosition AND :newPosition")
    void updateItemPositionsIncrement(int oldPosition, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + ITEM_COLUMN_POSITION + " = " + ITEM_COLUMN_POSITION + " - 1"
            + " WHERE " + ITEM_COLUMN_POSITION + " BETWEEN :oldPosition AND :newPosition")
    void updateItemPositionsDecrement(int oldPosition, int newPosition);
}

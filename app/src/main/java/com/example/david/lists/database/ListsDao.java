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
public interface ListsDao {
    @Query("SELECT * FROM " + USER_LIST_TABLE_NAME
            + " ORDER BY " + USER_LIST_COLUMN_POSITION)
    Flowable<List<UserList>> getAllLists();

    @Query("SELECT * FROM " + ITEM_TABLE_NAME
            + " WHERE " + ITEM_COLUMN_LIST_ID + " = :listId"
            + " ORDER BY " + ITEM_COLUMN_POSITION)
    Flowable<List<Item>> getListItems(int listId);


    @Insert(onConflict = REPLACE)
    long addList(UserList list);

    @Insert(onConflict = REPLACE)
    long addItem(Item item);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + USER_LIST_COLUMN_NAME + " = :newTitle"
            + " WHERE " + USER_LIST_COLUMN_ID + " = :listId")
    void changeListTitle(int listId, String newTitle);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + ITEM_COLUMN_NAME + " = :newTitle"
            + " WHERE " + ITEM_COLUMN_ID + " = :itemId")
    void changeItemTitle(int itemId, String newTitle);


    @Query("DELETE FROM " + USER_LIST_TABLE_NAME + " WHERE " + USER_LIST_COLUMN_ID + " = :listId")
    int deleteList(int listId);

    @Query("DELETE FROM " + ITEM_TABLE_NAME + " WHERE " + ITEM_COLUMN_ID + " = :itemId")
    int deleteItem(int itemId);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + USER_LIST_COLUMN_POSITION + " = :newPosition"
            + " WHERE " + USER_LIST_COLUMN_ID + " = :listId")
    void moveListPosition(int listId, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + ITEM_COLUMN_POSITION + " = :newPosition"
            + " WHERE " + ITEM_COLUMN_ID + " = :itemId")
    void moveItemPosition(int itemId, int newPosition);
}

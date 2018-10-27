package com.example.david.lists.data.local;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Flowable;

import static androidx.room.OnConflictStrategy.REPLACE;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ITEM_USER_LIST_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.local.LocalDatabaseConstants.ITEM_TABLE_NAME;
import static com.example.david.lists.data.local.LocalDatabaseConstants.USER_LIST_TABLE_NAME;

@Dao
public interface LocalDao {
    @Query("SELECT * FROM " + USER_LIST_TABLE_NAME
            + " ORDER BY " + FIELD_POSITION)
    Flowable<List<UserList>> getAllUserLists();

    @Query("SELECT * FROM " + ITEM_TABLE_NAME
            + " WHERE " + FIELD_ITEM_USER_LIST_ID + " = :userListId"
            + " ORDER BY " + FIELD_POSITION)
    Flowable<List<Item>> getAllItems(int userListId);


    @Insert(onConflict = REPLACE)
    void addUserList(UserList list);

    @Insert(onConflict = REPLACE)
    void addItem(Item item);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + FIELD_TITLE + " = :newTitle"
            + " WHERE " + FIELD_ID + " = :userListId")
    void renameUserList(int userListId, String newTitle);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + FIELD_TITLE + " = :newTitle"
            + " WHERE " + FIELD_ID + " = :itemId")
    void renameItem(int itemId, String newTitle);


    @Query("DELETE FROM " + USER_LIST_TABLE_NAME + " WHERE " + FIELD_ID + " IN (:userListIds)")
    void deleteUserList(List<Integer> userListIds);

    @Query("DELETE FROM " + ITEM_TABLE_NAME + " WHERE " + FIELD_ID + " IN (:itemIds)")
    void deleteItem(List<Integer> itemIds);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + FIELD_POSITION + " = :newPosition"
            + " WHERE " + FIELD_ID + " = :userListId")
    void updateUserListPosition(int userListId, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + FIELD_POSITION + " = :newPosition"
            + " WHERE " + FIELD_ID + " = :itemId")
    void updateItemPosition(int itemId, int newPosition);


    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + FIELD_POSITION + " = " + FIELD_POSITION + " + 1"
            + " WHERE " + FIELD_POSITION + " BETWEEN :newPosition AND :oldPosition")
    void updateUserListPositionsIncrement(int oldPosition, int newPosition);

    @Query("UPDATE " + USER_LIST_TABLE_NAME
            + " SET " + FIELD_POSITION + " = " + FIELD_POSITION + " - 1"
            + " WHERE " + FIELD_POSITION + " BETWEEN :oldPosition AND :newPosition")
    void updateUserListPositionsDecrement(int oldPosition, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + FIELD_POSITION + " = " + FIELD_POSITION + " + 1"
            + " WHERE " + FIELD_POSITION + " BETWEEN :oldPosition AND :newPosition")
    void updateItemPositionsIncrement(int oldPosition, int newPosition);

    @Query("UPDATE " + ITEM_TABLE_NAME
            + " SET " + FIELD_POSITION + " = " + FIELD_POSITION + " - 1"
            + " WHERE " + FIELD_POSITION + " BETWEEN :oldPosition AND :newPosition")
    void updateItemPositionsDecrement(int oldPosition, int newPosition);
}

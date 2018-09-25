package com.example.david.lists.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.RESTRICT;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_ID;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_LIST_ID;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_NAME;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_POSITION;
import static com.example.david.lists.database.DatabaseContract.ITEM_TABLE_NAME;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_ID;

@Entity(tableName = ITEM_TABLE_NAME,
        indices = @Index(ITEM_COLUMN_LIST_ID),
        foreignKeys = @ForeignKey(
                entity = UserList.class,
                parentColumns = USER_LIST_COLUMN_ID,
                childColumns = ITEM_COLUMN_LIST_ID,
                onUpdate = RESTRICT,
                onDelete = CASCADE
        ))
public final class Item {

    @PrimaryKey
    @ColumnInfo(name = ITEM_COLUMN_ID)
    private final int id;

    @ColumnInfo(name = ITEM_COLUMN_NAME)
    private final String name;

    @ColumnInfo(name = ITEM_COLUMN_POSITION)
    private final int position;

    @ColumnInfo(name = ITEM_COLUMN_LIST_ID)
    private final int listId;


    public Item(int id, String name, int position, int listId) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.listId = listId;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public int getListId() {
        return listId;
    }
}

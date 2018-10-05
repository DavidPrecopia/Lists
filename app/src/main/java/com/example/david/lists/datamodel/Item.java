package com.example.david.lists.datamodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.RESTRICT;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_ID;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_LIST_ID;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_NAME;
import static com.example.david.lists.database.DatabaseContract.ITEM_COLUMN_POSITION;
import static com.example.david.lists.database.DatabaseContract.ITEM_TABLE_NAME;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_ID;

@Entity(tableName = ITEM_TABLE_NAME,
        indices = {@Index(ITEM_COLUMN_ID),
                @Index(ITEM_COLUMN_POSITION),
                @Index(ITEM_COLUMN_LIST_ID)
        },
        foreignKeys = @ForeignKey(
                entity = UserList.class,
                parentColumns = USER_LIST_COLUMN_ID,
                childColumns = ITEM_COLUMN_LIST_ID,
                onUpdate = RESTRICT,
                onDelete = CASCADE
        ))
public final class Item {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ITEM_COLUMN_ID)
    private int id;

    @ColumnInfo(name = ITEM_COLUMN_NAME)
    private final String title;

    @ColumnInfo(name = ITEM_COLUMN_POSITION)
    private final int position;

    @ColumnInfo(name = ITEM_COLUMN_LIST_ID)
    private final int listId;


    public Item(int id, String title, int position, int listId) {
        this.id = id;
        this.title = title;
        this.position = position;
        this.listId = listId;
    }

    @Ignore
    public Item(String title, int position, int listId) {
        this.title = title;
        this.position = position;
        this.listId = listId;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    public int getListId() {
        return listId;
    }
}

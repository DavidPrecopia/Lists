package com.example.david.lists.datamodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_ID;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_NAME;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_POSITION;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_TABLE_NAME;

@Entity(tableName = USER_LIST_TABLE_NAME,
        indices = {@Index(value = {USER_LIST_COLUMN_POSITION}, unique = true)}
)
public final class UserList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = USER_LIST_COLUMN_ID)
    private int id;

    @ColumnInfo(name = USER_LIST_COLUMN_NAME)
    private final String title;

    @ColumnInfo(name = USER_LIST_COLUMN_POSITION)
    private final int position;


    public UserList(int id, String title, int position) {
        this.id = id;
        this.title = title;
        this.position = position;
    }

    @Ignore
    public UserList(String title, int position) {
        this.title = title;
        this.position = position;
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
}

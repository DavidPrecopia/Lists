package com.example.david.lists.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_ID;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_NAME;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_COLUMN_POSITION;
import static com.example.david.lists.database.DatabaseContract.USER_LIST_TABLE_NAME;

@Entity(tableName = USER_LIST_TABLE_NAME)
public final class UserList {

    @PrimaryKey
    @ColumnInfo(name = USER_LIST_COLUMN_ID)
    private final int id;

    @ColumnInfo(name = USER_LIST_COLUMN_NAME)
    private final String name;

    @ColumnInfo(name = USER_LIST_COLUMN_POSITION)
    private final int position;


    public UserList(int id, String name, int position) {
        this.id = id;
        this.name = name;
        this.position = position;
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
}

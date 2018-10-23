package com.example.david.lists.data.datamodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static com.example.david.lists.data.local.LocalDatabaseConstants.USER_LIST_COLUMN_ID;
import static com.example.david.lists.data.local.LocalDatabaseConstants.USER_LIST_COLUMN_NAME;
import static com.example.david.lists.data.local.LocalDatabaseConstants.USER_LIST_COLUMN_POSITION;
import static com.example.david.lists.data.local.LocalDatabaseConstants.USER_LIST_TABLE_NAME;

@Entity(tableName = USER_LIST_TABLE_NAME,
        indices = {@Index(USER_LIST_COLUMN_ID),
                @Index(USER_LIST_COLUMN_POSITION)
        }
)
public final class UserList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = USER_LIST_COLUMN_ID)
    private int id;

    @ColumnInfo(name = USER_LIST_COLUMN_NAME)
    private String title;

    @ColumnInfo(name = USER_LIST_COLUMN_POSITION)
    private int position;


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

    @Ignore
    public UserList() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }
}

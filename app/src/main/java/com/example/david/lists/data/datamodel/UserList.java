package com.example.david.lists.data.datamodel;

import com.example.david.lists.data.local.LocalDatabaseConstants;
import com.google.firebase.firestore.Exclude;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.local.LocalDatabaseConstants.COLUMN_ROW_ID;

/**
 * Field names need to match the constants in
 * {@link DataModelFieldConstants}.
 */
@Entity(tableName = LocalDatabaseConstants.USER_LIST_TABLE_NAME,
        indices = {@Index(value = FIELD_ID, unique = true),
                @Index(FIELD_POSITION)
        }
)
public final class UserList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ROW_ID)
    private int rowId;

    @ColumnInfo(name = FIELD_ID)
    private String id;

    @ColumnInfo(name = FIELD_TITLE)
    private String title;

    @ColumnInfo(name = FIELD_POSITION)
    private int position;


    public UserList(int rowId, String id, String title, int position) {
        this.rowId = rowId;
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
    public UserList(String id, UserList userList) {
        this.id = id;
        this.title = userList.title;
        this.position = userList.position;
    }

    @Ignore
    public UserList() {
    }


    @Exclude
    public int getRowId() {
        return rowId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }
}

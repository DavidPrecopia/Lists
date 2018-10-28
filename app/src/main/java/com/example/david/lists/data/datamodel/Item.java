package com.example.david.lists.data.datamodel;

import com.example.david.lists.data.local.LocalDatabaseConstants;
import com.google.firebase.firestore.Exclude;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.RESTRICT;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ITEM_USER_LIST_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.local.LocalDatabaseConstants.COLUMN_ROW_ID;

/**
 * Field names need to match the constants in
 * {@link DataModelFieldConstants}.
 */
@Entity(tableName = LocalDatabaseConstants.ITEM_TABLE_NAME,
        indices = {@Index(value = FIELD_ID, unique = true),
                @Index(FIELD_POSITION),
                @Index(FIELD_ITEM_USER_LIST_ID)
        },
        foreignKeys = @ForeignKey(
                entity = UserList.class,
                parentColumns = FIELD_ID,
                childColumns = FIELD_ITEM_USER_LIST_ID,
                onUpdate = RESTRICT,
                onDelete = CASCADE
        ))
public final class Item {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ROW_ID)
    private int rowId;

    @ColumnInfo(name = FIELD_ID)
    private String id;

    @ColumnInfo(name = FIELD_TITLE)
    private String title;

    @ColumnInfo(name = FIELD_POSITION)
    private int position;

    @ColumnInfo(name = FIELD_ITEM_USER_LIST_ID)
    private String userListId;


    public Item(int rowId, String id, String title, int position, String userListId) {
        this.rowId = rowId;
        this.id = id;
        this.title = title;
        this.position = position;
        this.userListId = userListId;
    }

    @Ignore
    public Item(String title, int position, String userListId) {
        this.title = title;
        this.position = position;
        this.userListId = userListId;
    }

    @Ignore
    public Item(String id, Item item) {
        this.id = id;
        this.title = item.title;
        this.position = item.position;
        this.userListId = item.userListId;
    }

    @Ignore
    public Item() {
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

    public String getUserListId() {
        return userListId;
    }
}

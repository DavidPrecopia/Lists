package com.example.david.lists.data.datamodel;

import com.example.david.lists.data.local.LocalDatabaseConstants;

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

/**
 * Field names need to kept be in sync with
 * {@link DataModelFieldConstants}.
 */
@Entity(tableName = LocalDatabaseConstants.ITEM_TABLE_NAME,
        indices = {@Index(FIELD_ID),
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
    @ColumnInfo(name = FIELD_ID)
    private int id;

    @ColumnInfo(name = FIELD_TITLE)
    private String title;

    @ColumnInfo(name = FIELD_POSITION)
    private int position;

    @ColumnInfo(name = FIELD_ITEM_USER_LIST_ID)
    private int userListId;


    public Item(int id, String title, int position, int userListId) {
        this.id = id;
        this.title = title;
        this.position = position;
        this.userListId = userListId;
    }

    @Ignore
    public Item(String title, int position, int userListId) {
        this.title = title;
        this.position = position;
        this.userListId = userListId;
    }

    @Ignore
    public Item() {
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

    public int getUserListId() {
        return userListId;
    }
}

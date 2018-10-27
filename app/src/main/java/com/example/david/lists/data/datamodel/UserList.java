package com.example.david.lists.data.datamodel;

import com.example.david.lists.data.local.LocalDatabaseConstants;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;

/**
 * Field names need to kept be in sync with
 * {@link DataModelFieldConstants}.
 */
@Entity(tableName = LocalDatabaseConstants.USER_LIST_TABLE_NAME,
        indices = {@Index(FIELD_ID),
                @Index(FIELD_POSITION)
        }
)
public final class UserList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FIELD_ID)
    private int id;

    @ColumnInfo(name = FIELD_TITLE)
    private String title;

    @ColumnInfo(name = FIELD_POSITION)
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

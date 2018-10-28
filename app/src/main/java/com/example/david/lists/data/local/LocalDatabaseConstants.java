package com.example.david.lists.data.local;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

public final class LocalDatabaseConstants {
    private LocalDatabaseConstants() {
    }

    static final String DATABASE_NAME = "lists.db";

    /**
     * {@link UserList}
     */
    public static final String USER_LIST_TABLE_NAME = "user_lists";

    /**
     * {@link Item}
     */
    public static final String ITEM_TABLE_NAME = "items";

    public static final String COLUMN_ROW_ID = "row_id";
}

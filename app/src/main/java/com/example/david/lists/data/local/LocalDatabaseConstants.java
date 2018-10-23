package com.example.david.lists.data.local;

public final class LocalDatabaseConstants {
    private LocalDatabaseConstants() {
    }

    static final String DATABASE_NAME = "lists.db";

    // UserList
    public static final String USER_LIST_TABLE_NAME = "user_lists";
    public static final String USER_LIST_COLUMN_ID = "id";
    public static final String USER_LIST_COLUMN_NAME = "name";
    public static final String USER_LIST_COLUMN_POSITION = "position";

    // Item
    public static final String ITEM_TABLE_NAME = "items";
    public static final String ITEM_COLUMN_ID = "id";
    public static final String ITEM_COLUMN_NAME = "name";
    public static final String ITEM_COLUMN_POSITION = "position";
    public static final String ITEM_COLUMN_LIST_ID = "list_id";
}

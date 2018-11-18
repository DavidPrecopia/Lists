package com.example.david.lists.data.datamodel;

/**
 * Field names need to match the constants in {@link DataModelFieldConstants}.
 */
public final class Item {

    private String id;

    private String userId;

    private String title;

    private int position;

    private String userListId;


    public Item(String id, String userId, String title, int position, String userListId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.position = position;
        this.userListId = userListId;
    }

    public Item(String title, int position, String userListId) {
        this.title = title;
        this.position = position;
        this.userListId = userListId;
    }

    public Item(String id, String userId, Item item) {
        this.id = id;
        this.userId = userId;
        this.title = item.title;
        this.position = item.position;
        this.userListId = item.userListId;
    }

    public Item() {
    }


    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
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

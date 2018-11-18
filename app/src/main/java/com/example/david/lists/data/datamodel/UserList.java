package com.example.david.lists.data.datamodel;

/**
 * Field names need to match the constants in {@link DataModelFieldConstants}.
 */
public final class UserList {

    private String id;

    private String userId;

    private String title;

    private int position;


    public UserList(String id, String userId, String title, int position) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.position = position;
    }

    public UserList(String title, int position) {
        this.title = title;
        this.position = position;
    }

    public UserList(String id, String userId, UserList userList) {
        this.id = id;
        this.userId = userId;
        this.title = userList.title;
        this.position = userList.position;
    }

    public UserList() {
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
}

package com.example.david.lists.data.datamodel;

/**
 * Field names need to match the constants in {@link DataModelFieldConstants}.
 */
public final class Group {

    private String id;
    private String userId;
    private String title;
    private int position;

    public Group(String id, String userId, String title, int position) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.position = position;
    }

    public Group(String title, int position) {
        this.title = title;
        this.position = position;
    }

    public Group(String id, String userId, Group group) {
        this.id = id;
        this.userId = userId;
        this.title = group.title;
        this.position = group.position;
    }

    public Group() {
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

package com.example.david.lists.data.datamodel;

/**
 * Field names need to match the constants in {@link DataModelFieldConstants}.
 */
public final class Group {

    private String id;
    private String title;
    private int position;

    public Group(String title, int position) {
        this.title = title;
        this.position = position;
    }

    public Group(String id, Group group) {
        this.id = id;
        this.title = group.title;
        this.position = group.position;
    }

    public Group() {
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

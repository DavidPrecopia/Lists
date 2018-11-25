package com.example.david.lists.data.datamodel;

/**
 * Field names need to match the constants in {@link DataModelFieldConstants}.
 */
public final class Item {

    private String id;
    private String title;
    private int position;

    private String groupId;

    public Item(String title, int position, String groupId) {
        this.title = title;
        this.position = position;
        this.groupId = groupId;
    }

    public Item(String id, Item item) {
        this.id = id;
        this.title = item.title;
        this.position = item.position;
        this.groupId = item.groupId;
    }

    public Item() {
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

    public String getGroupId() {
        return groupId;
    }
}

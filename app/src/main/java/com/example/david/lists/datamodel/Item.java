package com.example.david.lists.datamodel;

public final class Item {

    private final int id;
    private final String name;
    private final int position;
    private final int listId;

    public Item(int id, String name, int position, int listId) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.listId = listId;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public int getListId() {
        return listId;
    }
}

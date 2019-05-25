package com.example.david.lists.view.itemlist;

import com.example.david.lists.data.datamodel.Item;

import java.util.List;

public interface IItemAdapter {
    void submitList(List<Item> list);

    void move(int fromPosition, int toPosition);

    void remove(int position);

    void reAdd(int position, Item item);
}

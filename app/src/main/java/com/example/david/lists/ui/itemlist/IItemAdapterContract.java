package com.example.david.lists.ui.itemlist;

import com.example.david.lists.data.datamodel.Item;

import java.util.List;

public interface IItemAdapterContract {
    void submitList(List<Item> list);

    void move(int fromPosition, int toPosition);

    void remove(int position);

    void reAdd(int position, Item item);
}

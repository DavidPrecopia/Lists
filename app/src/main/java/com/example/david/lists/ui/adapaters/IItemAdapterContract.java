package com.example.david.lists.ui.adapaters;

import com.example.david.lists.data.datamodel.Item;

public interface IItemAdapterContract {
    void move(int fromPosition, int toPosition);

    void remove(int position);

    void reAdd(int position, Item item);
}

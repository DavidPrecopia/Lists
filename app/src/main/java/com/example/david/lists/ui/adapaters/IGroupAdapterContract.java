package com.example.david.lists.ui.adapaters;

import com.example.david.lists.data.datamodel.Group;

public interface IGroupAdapterContract {
    void move(int fromPosition, int toPosition);

    void remove(int position);

    void reAdd(int position, Group group);
}

package com.example.david.lists.ui.adapaters;

import com.example.david.lists.data.datamodel.UserList;

public interface IUserListAdapterContract {
    void move(int fromPosition, int toPosition);

    void remove(int position);

    void reAdd(int position, UserList userList);
}

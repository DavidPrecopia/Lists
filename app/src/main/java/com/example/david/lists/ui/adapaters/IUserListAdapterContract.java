package com.example.david.lists.ui.adapaters;

import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IUserListAdapterContract {
    void submitList(List<UserList> list);

    void move(int fromPosition, int toPosition);

    void remove(int position);

    void reAdd(int position, UserList userList);
}

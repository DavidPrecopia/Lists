package com.example.david.lists.view.userlistlist;

import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IUserListAdapter {
    void submitList(List<UserList> list);

    void move(int fromPosition, int toPosition);

    void remove(int position);

    void reAdd(int position, UserList userList);
}

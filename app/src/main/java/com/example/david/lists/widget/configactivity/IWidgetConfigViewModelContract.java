package com.example.david.lists.widget.configactivity;

import com.example.david.lists.data.datamodel.UserList;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

interface IWidgetConfigViewModelContract {
    void userListClicked(UserList userList);

    RecyclerView.Adapter getAdapter();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<UserList> getEventOpenUserList();

    LiveData<String> getEventDisplayError();
}

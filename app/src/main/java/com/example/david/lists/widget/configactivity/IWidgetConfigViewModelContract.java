package com.example.david.lists.widget.configactivity;

import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

import androidx.lifecycle.LiveData;

interface IWidgetConfigViewModelContract {
    void userListClicked(UserList userList);

    LiveData<List<UserList>> getUserLists();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<Void> getEventSuccessful();

    LiveData<Boolean> getEventDisplayError();

    LiveData<String> getErrorMessage();
}

package com.example.david.lists.widget.configactivity;

import androidx.lifecycle.LiveData;

import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IWidgetConfigViewModel {
    void userListClicked(UserList userList);

    LiveData<List<UserList>> getUserLists();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<Void> getEventSuccessful();

    LiveData<Boolean> getEventDisplayError();

    LiveData<String> getErrorMessage();
}

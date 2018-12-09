package com.example.david.lists.widget.configactivity;

import com.example.david.lists.data.datamodel.Group;

import java.util.List;

import androidx.lifecycle.LiveData;

interface IWidgetConfigViewModelContract {
    void groupClicked(Group group);

    LiveData<List<Group>> getGroupList();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<Void> getEventSuccessful();

    LiveData<Boolean> getEventDisplayError();

    LiveData<String> getErrorMessage();
}

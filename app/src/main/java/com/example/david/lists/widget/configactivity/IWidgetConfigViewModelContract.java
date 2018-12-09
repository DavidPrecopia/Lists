package com.example.david.lists.widget.configactivity;

import com.example.david.lists.data.datamodel.Group;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

interface IWidgetConfigViewModelContract {
    void groupClicked(Group group);

    RecyclerView.Adapter getAdapter();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<Void> getEventSuccessful();

    LiveData<String> getEventDisplayError();
}

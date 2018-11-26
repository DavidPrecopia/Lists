package com.example.david.lists.ui.viewmodels;

import com.example.david.lists.data.datamodel.EditingInfo;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public interface IItemViewModelContract {
    void addButtonClicked();

    void add(String title);

    void changeTitle(EditingInfo editingInfo, String newTitle);

    void undoRecentDeletion();

    void deletionNotificationTimedOut();

    RecyclerView.Adapter getAdapter();

    ItemTouchHelper getItemTouchHelper();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<String> getEventDisplayError();

    LiveData<String> getEventNotifyUserOfDeletion();

    LiveData<String> getEventAdd();

    LiveData<EditingInfo> getEventEdit();

    LiveData<Void> getEventFinish();
}

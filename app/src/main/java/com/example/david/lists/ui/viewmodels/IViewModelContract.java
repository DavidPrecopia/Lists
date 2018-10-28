package com.example.david.lists.ui.viewmodels;

import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.UserList;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public interface IViewModelContract {
    void userListClicked(UserList userList);

    void addButtonClicked();

    void add(String title);

    void dragging(int fromPosition, int toPosition);

    void movedPermanently(int newPosition);

    void edit(int position);

    void changeTitle(String id, String newTitle);

    void delete(int position);

    void swipedLeft(int position);

    void undoRecentDeletion();

    void deletionNotificationTimedOut();

    void refresh();

    RecyclerView.Adapter getAdapter();

    ItemTouchHelper getItemTouchHelper();

    LiveData<String> getToolbarTitle();

    LiveData<UserList> getEventOpenUserList();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<String> getEventDisplayError();

    LiveData<String> getEventNotifyUserOfDeletion();

    LiveData<String> getEventAdd();

    LiveData<EditingInfo> getEventEdit();
}

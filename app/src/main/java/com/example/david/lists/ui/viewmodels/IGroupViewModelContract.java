package com.example.david.lists.ui.viewmodels;

import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Group;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public interface IGroupViewModelContract {
    void groupClicked(Group group);

    void addButtonClicked();

    void add(String title);

    void changeTitle(EditingInfo editingInfo, String newTitle);

    void undoRecentDeletion();

    void deletionNotificationTimedOut();

    void signIn();

    void signOut();

    RecyclerView.Adapter getAdapter();

    ItemTouchHelper getItemTouchHelper();

    LiveData<Group> getEventOpenGroup();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<String> getEventDisplayError();

    LiveData<String> getEventNotifyUserOfDeletion();

    LiveData<String> getEventAdd();

    LiveData<EditingInfo> getEventEdit();

    LiveData<Void> getEventSignOut();

    LiveData<Void> getEventSignIn();
}

package com.example.david.lists.ui.viewmodels;

import android.view.MenuItem;

import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.ui.adapaters.IGroupAdapterContract;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface IGroupViewModelContract {
    void groupClicked(Group group);

    void addButtonClicked();

    void add(String title);

    void edit(int position);

    void changeTitle(EditingInfo editingInfo, String newTitle);

    void dragging(IGroupAdapterContract adapter, int fromPosition, int toPosition);

    void movedPermanently(int newPosition);

    void swipedLeft(IGroupAdapterContract adapter, int position);

    void delete(IGroupAdapterContract adapter, int position);

    void undoRecentDeletion(IGroupAdapterContract adapter);

    void deletionNotificationTimedOut();

    void nightMode(MenuItem item);

    void signIn();

    void signOut();


    LiveData<List<Group>> getGroupList();

    LiveData<Group> getEventOpenGroup();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<Boolean> getEventDisplayError();

    LiveData<String> getErrorMessage();

    LiveData<String> getEventNotifyUserOfDeletion();

    LiveData<String> getEventAdd();

    LiveData<EditingInfo> getEventEdit();

    LiveData<Void> getEventSignOut();

    LiveData<Void> getEventSignIn();
}

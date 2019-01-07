package com.example.david.lists.ui.viewmodels;

import android.view.MenuItem;

import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.ui.adapaters.IUserListAdapterContract;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface IUserListViewModelContract {
    void userListClicked(UserList userList);

    void addButtonClicked();

    void add(String title);

    void edit(int position);

    void changeTitle(EditingInfo editingInfo, String newTitle);

    void dragging(IUserListAdapterContract adapter, int fromPosition, int toPosition);

    void movedPermanently(int newPosition);

    void swipedLeft(IUserListAdapterContract adapter, int position);

    void delete(IUserListAdapterContract adapter, int position);

    void undoRecentDeletion(IUserListAdapterContract adapter);

    void deletionNotificationTimedOut();

    void nightMode(MenuItem item);

    void signIn();

    void signOutButtonClicked();

    void signOut();


    LiveData<List<UserList>> getUserLists();

    LiveData<UserList> getEventOpenUserList();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<Boolean> getEventDisplayError();

    LiveData<String> getErrorMessage();

    LiveData<String> getEventNotifyUserOfDeletion();

    LiveData<String> getEventAdd();

    LiveData<EditingInfo> getEventEdit();

    LiveData<Void> getEventSignOut();

    LiveData<Void> getEventConfirmSignOut();

    LiveData<Void> getEventSignIn();
}

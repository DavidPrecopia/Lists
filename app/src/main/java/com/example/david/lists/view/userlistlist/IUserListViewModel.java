package com.example.david.lists.view.userlistlist;

import android.view.MenuItem;

import androidx.lifecycle.LiveData;

import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IUserListViewModel {
    void userListClicked(UserList userList);

    void addButtonClicked();

    void edit(UserList userList);

    void dragging(IUserListAdapter adapter, int fromPosition, int toPosition);

    void movedPermanently(int newPosition);

    void swipedLeft(IUserListAdapter adapter, int position);

    void delete(IUserListAdapter adapter, int position);

    void undoRecentDeletion(IUserListAdapter adapter);

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

    LiveData<Void> getEventAdd();

    LiveData<UserList> getEventEdit();

    LiveData<Void> getEventSignOut();

    LiveData<Void> getEventConfirmSignOut();

    LiveData<Void> getEventSignIn();
}

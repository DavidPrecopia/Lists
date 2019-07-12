package com.example.david.lists.view.userlistlist;

import android.view.MenuItem;

import androidx.lifecycle.LiveData;

import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IUserListViewContract {
    interface ViewModel {
        void userListClicked(UserList userList);

        void addButtonClicked();

        void edit(UserList userList);

        void dragging(Adapter adapter, int fromPosition, int toPosition);

        void movedPermanently(int newPosition);

        void swipedLeft(Adapter adapter, int position);

        void delete(Adapter adapter, int position);

        void undoRecentDeletion(Adapter adapter);

        void deletionNotificationTimedOut();

        void onOptionsItemSelected(MenuItem menuItem);

        void signOut();

        int getMenuResource();


        LiveData<List<UserList>> getUserLists();

        LiveData<UserList> getEventOpenUserList();

        LiveData<Boolean> getEventDisplayLoading();

        LiveData<Boolean> getEventDisplayError();

        LiveData<String> getErrorMessage();

        LiveData<String> getEventNotifyUserOfDeletion();

        LiveData<Void> getEventAdd();

        LiveData<UserList> getEventEdit();

        LiveData<Void> getEventSignOut();

        LiveData<Void> getEventSignIn();
    }

    interface Adapter {
        void submitList(List<UserList> list);

        void move(int fromPosition, int toPosition);

        void remove(int position);

        void reAdd(int position, UserList userList);
    }
}

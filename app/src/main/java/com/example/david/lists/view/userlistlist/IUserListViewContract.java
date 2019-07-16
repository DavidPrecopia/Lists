package com.example.david.lists.view.userlistlist;

import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IUserListViewContract {
    interface View {
        void openUserList(Intent intent);

        void openAuthentication(Intent intent, int requestCode);

        void openDialog(DialogFragment dialog);

        void notifyUserOfDeletion(String message);

        void setStateDisplayList();

        void setStateLoading();

        void setStateError(String message);

        void recreateView();
    }

    interface Logic {
        void onStart();

        RecyclerView.Adapter getAdapter();

        void userListSelected(UserList userList);

        void add();

        void edit(UserList userList);

        void dragging(int fromPosition, int toPosition);

        void movedPermanently(int newPosition);

        void delete(int position);

        void undoRecentDeletion();

        void deletionNotificationTimedOut();

        void signOut();

        void signOutConfirmed();

        void signIn();

        void authResult(int requestCode, Intent data);

        void nightMode(MenuItem item);

        int getMenuResource();

        void onDestroy();
    }

    interface Adapter {
        void init(IUserListViewContract.Logic logic);

        void submitList(List<UserList> list);

        void move(int fromPosition, int toPosition);

        void remove(int position);

        void reAdd(int position, UserList userList);
    }
}

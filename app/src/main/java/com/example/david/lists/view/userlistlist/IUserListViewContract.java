package com.example.david.lists.view.userlistlist;

import android.content.Intent;
import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.view.authentication.IAuthContract;

import java.util.List;

public interface IUserListViewContract {
    interface View {
        void openUserList(UserList userList);

        void confirmSignOut();

        void openAuthentication(IAuthContract.AuthGoal authGoal, int requestCode);

        void openAddDialog(int position);

        void openEditDialog(UserList userList);

        void notifyUserOfDeletion(String message);

        void setStateDisplayList();

        void setStateLoading();

        void setStateError(String message);

        void recreateView();
    }

    interface Logic {
        void onStart();

        RecyclerView.Adapter getAdapter();

        void userListSelected(int position);

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

    interface ViewModel {
        void setViewData(List<UserList> userLists);

        List<UserList> getViewData();

        void setTempPosition(int position);

        List<UserList> getTempList();

        int getTempPosition();

        String getIntentExtraAuthResultKey();

        String getMsgInvalidUndo();

        String getMsgDeletion();

        String getErrorMsg();

        String getErrorMsgEmptyList();
    }

    interface Adapter {
        void init(IUserListViewContract.Logic logic);

        void submitList(List<UserList> list);

        void move(int fromPosition, int toPosition);

        void remove(int position);

        void reAdd(int position, UserList userList);
    }
}

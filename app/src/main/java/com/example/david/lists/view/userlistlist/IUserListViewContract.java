package com.example.david.lists.view.userlistlist;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.view.authentication.IAuthContract;

import java.util.List;

public interface IUserListViewContract {
    interface View {
        void openUserList(UserList userList);

        void confirmSignOut();

        void openAuthentication(IAuthContract.AuthGoal authGoal, int requestCode, String intentExtraAuthResultKey);

        void openAddDialog(int position);

        void openEditDialog(UserList userList);

        void submitList(List<UserList> viewData);

        void notifyUserOfDeletion(String message);

        void setStateDisplayList();

        void setStateLoading();

        void setStateError(String message);

        void recreateView();
    }

    interface Logic {
        void onStart();

        void userListSelected(int position);

        void add();

        void edit(UserList userList);

        void dragging(int fromPosition, int toPosition, IUserListViewContract.Adapter adapter);

        void movedPermanently(int newPosition);

        void delete(int position, IUserListViewContract.Adapter adapter);

        void undoRecentDeletion(Adapter adapter);

        void deletionNotificationTimedOut();

        void signOut();

        void signOutConfirmed();

        void signIn();

        void authResult(IAuthContract.AuthResult authResult);

        void setNightMode(boolean isMenuItemChecked);

        boolean isUserAnon();

        boolean isNightModeEnabled();

        void onDestroy();
    }

    interface ViewModel {
        void setViewData(List<UserList> userLists);

        List<UserList> getViewData();

        void setTempPosition(int position);

        List<UserList> getTempList();

        int getTempPosition();

        int getRequestCode();

        String getIntentExtraAuthResultKey();

        String getMsgInvalidUndo();

        String getMsgDeletion();

        String getErrorMsg();

        String getErrorMsgEmptyList();
    }

    interface Adapter {
        void submitList(List<UserList> list);

        void move(int fromPosition, int toPosition);

        void remove(int position);

        void reAdd(int position, UserList userList);
    }
}

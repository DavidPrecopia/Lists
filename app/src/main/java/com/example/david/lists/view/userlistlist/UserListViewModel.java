package com.example.david.lists.view.userlistlist;

import android.app.Application;
import android.content.Intent;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.view.authentication.AuthView;
import com.example.david.lists.view.authentication.IAuthContract;
import com.example.david.lists.view.itemlist.ItemActivity;

import java.util.ArrayList;
import java.util.List;

public class UserListViewModel implements IUserListViewContract.ViewModel {

    private final Application application;

    private List<UserList> userLists;

    private List<UserList> tempList;
    private int tempPosition;

    public UserListViewModel(Application application) {
        this.application = application;
        this.userLists = new ArrayList<>();
        this.tempList = new ArrayList<>();
        this.tempPosition = -1;
    }


    @Override
    public void setViewData(List<UserList> userLists) {
        this.userLists = userLists;
    }

    @Override
    public List<UserList> getViewData() {
        return userLists;
    }

    @Override
    public void setTempPosition(int position) {
        this.tempPosition = position;
    }

    @Override
    public List<UserList> getTempList() {
        return tempList;
    }

    @Override
    public int getTempPosition() {
        return tempPosition;
    }


    @Override
    public Intent getOpenUserListIntent(UserList userList) {
        Intent intent = new Intent(application, ItemActivity.class);
        intent.putExtra(getStringRes(R.string.intent_extra_user_list_id), userList.getId());
        intent.putExtra(getStringRes(R.string.intent_extra_user_list_title), userList.getTitle());
        return intent;
    }

    @Override
    public Intent getAuthIntent(IAuthContract.AuthGoal authGoal) {
        Intent intent = new Intent(application, AuthView.class);
        intent.putExtra(getStringRes(R.string.intent_extra_auth), authGoal);
        return intent;
    }


    @Override
    public String getIntentExtraAuthResultKey() {
        return getStringRes(R.string.intent_extra_auth_result);
    }


    @Override
    public String getMsgInvalidUndo() {
        return getStringRes(R.string.error_msg_invalid_action_undo_deletion);
    }

    @Override
    public String getMsgDeletion() {
        return getStringRes(R.string.msg_user_list_deletion);
    }

    @Override
    public String getErrorMsg() {
        return getStringRes(R.string.error_msg_generic);
    }

    @Override
    public String getErrorMsgEmptyList() {
        return getStringRes(R.string.error_msg_no_user_lists);
    }


    private String getStringRes(int resId) {
        return application.getString(resId);
    }
}

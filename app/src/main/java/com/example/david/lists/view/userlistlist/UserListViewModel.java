package com.example.david.lists.view.userlistlist;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;

import java.util.ArrayList;
import java.util.List;

public class UserListViewModel implements IUserListViewContract.ViewModel {

    private final Application application;
    private final int requestCode;

    private List<UserList> userLists;

    private List<UserList> tempList;
    private int tempPosition;

    public UserListViewModel(Application application, int requestCode) {
        this.application = application;
        this.requestCode = requestCode;
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
    public int getRequestCode() {
        return requestCode;
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

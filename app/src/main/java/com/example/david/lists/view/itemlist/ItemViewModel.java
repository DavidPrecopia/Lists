package com.example.david.lists.view.itemlist;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel implements IItemViewContract.ViewModel {

    private final Application application;

    private List<Item> viewData;
    private final String userListId;

    private final List<Item> tempList;
    private int tempPosition;

    public ItemViewModel(Application application, String userListId) {
        this.application = application;
        this.userListId = userListId;
        viewData = new ArrayList<>();
        tempList = new ArrayList<>();
        tempPosition = -1;
    }


    @Override
    public String getUserListId() {
        return userListId;
    }


    @Override
    public void setViewData(List<Item> items) {
        this.viewData = items;
    }

    @Override
    public List<Item> getViewData() {
        return viewData;
    }

    @Override
    public void setTempPosition(int position) {
        this.tempPosition = position;
    }

    @Override
    public List<Item> getTempList() {
        return tempList;
    }

    @Override
    public int getTempPosition() {
        return tempPosition;
    }


    @Override
    public String getMsgListDeleted(String title) {
        return getStringRes(R.string.msg_user_list_deletion_parameter, title);
    }

    @Override
    public String getMsgItemDeleted() {
        return getStringRes(R.string.msg_item_deletion);
    }

    @Override
    public String getErrorMsg() {
        return getStringRes(R.string.error_msg_generic);
    }

    @Override
    public String getErrorMsgEmptyList() {
        return getStringRes(R.string.error_msg_empty_user_list);
    }

    @Override
    public String getErrorMsgInvalidUndo() {
        return getStringRes(R.string.error_msg_invalid_action_undo_deletion);
    }


    private String getStringRes(int resId) {
        return application.getString(resId);
    }

    private String getStringRes(int resId, Object object) {
        return application.getString(resId, object);
    }
}

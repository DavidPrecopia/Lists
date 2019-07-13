package com.example.david.lists.widget.configview;

import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetManager;

import androidx.annotation.NonNull;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.widget.UtilWidgetKeys;

import java.util.ArrayList;
import java.util.List;

public final class WidgetConfigViewModel implements IWidgetConfigContract.ViewModel {

    @NonNull
    private final Application application;

    private List<UserList> userLists;
    private int widgetId;

    public WidgetConfigViewModel(@NonNull Application application) {
        this.application = application;
        this.userLists = new ArrayList<>();
    }


    @Override
    public void setViewData(List<UserList> list) {
        this.userLists = list;
    }

    @Override
    public List<UserList> getViewData() {
        return userLists;
    }


    @Override
    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    @Override
    public int getWidgetId() {
        return widgetId;
    }

    @Override
    public int getInvalidWidgetId() {
        return AppWidgetManager.INVALID_APPWIDGET_ID;
    }


    @Override
    public int getResultOk() {
        return Activity.RESULT_OK;
    }

    @Override
    public int getResultCancelled() {
        return Activity.RESULT_CANCELED;
    }


    @Override
    public String getErrorMsg() {
        return application.getString(R.string.error_msg_generic);
    }

    @Override
    public String getErrorMsgEmptyList() {
        return application.getString(R.string.error_msg_empty_user_list);
    }


    @Override
    public String getSharedPrefKeyId() {
        return UtilWidgetKeys.getSharedPrefKeyId(application, this.widgetId);
    }

    @Override
    public String getSharedPrefKeyTitle() {
        return UtilWidgetKeys.getSharedPrefKeyTitle(application, this.widgetId);
    }
}

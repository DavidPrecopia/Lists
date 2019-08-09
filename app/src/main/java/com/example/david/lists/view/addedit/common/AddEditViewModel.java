package com.example.david.lists.view.addedit.common;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.david.lists.R;
import com.example.david.lists.util.UtilExceptions;

import javax.annotation.Nullable;

public class AddEditViewModel implements IAddEditContract.ViewModel {

    @NonNull
    private final Application application;

    private IAddEditContract.TaskType currentTaskType;

    private String id;
    private String currentTitle;
    @Nullable
    private String userListId;

    private int position;

    public AddEditViewModel(@NonNull Application application) {
        this.application = application;
    }


    @Override
    public void setTaskType(IAddEditContract.TaskType taskType) {
        this.currentTaskType = taskType;
    }

    @Override
    public IAddEditContract.TaskType getTaskType() {
        return currentTaskType;
    }


    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setCurrentTitle(String title) {
        this.currentTitle = title;
    }

    @Override
    public void setUserListId(@Nullable String userListId) {
        this.userListId = userListId;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCurrentTitle() {
        return currentTitle;
    }

    @Override
    public String getUserListId() {
        if (TextUtils.isEmpty(userListId)) {
            UtilExceptions.throwException(new IllegalStateException("UserListId has not been initialized"));
        }
        return userListId;
    }

    @Override
    public int getPosition() {
        return position;
    }


    @Override
    public String getMsgEmptyTitle() {
        return application.getString(R.string.error_msg_empty_title_text_field);
    }

    @Override
    public String getMsgTitleUnchanged() {
        return application.getString(R.string.error_msg_title_unchanged);
    }
}

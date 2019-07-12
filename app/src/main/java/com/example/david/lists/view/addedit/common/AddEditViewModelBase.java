package com.example.david.lists.view.addedit.common;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.david.lists.R;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.util.SingleLiveEvent;

import io.reactivex.disposables.CompositeDisposable;

public abstract class AddEditViewModelBase extends AndroidViewModel {

    private static int taskType;
    protected static final int TASK_ADD = 100;
    protected static final int TASK_EDIT = 200;

    protected final IRepository repository;
    protected final CompositeDisposable disposable;

    private final String currentTitle;

    private final SingleLiveEvent<String> eventErrorMessage;
    private final SingleLiveEvent<Void> eventDismiss;

    public AddEditViewModelBase(@NonNull Application application, IRepository repository, CompositeDisposable disposable, String currentTitle) {
        super(application);
        this.repository = repository;
        this.disposable = disposable;
        this.currentTitle = currentTitle;
        this.eventErrorMessage = new SingleLiveEvent<>();
        this.eventDismiss = new SingleLiveEvent<>();
        setTaskType();
    }

    private void setTaskType() {
        if (TextUtils.isEmpty(currentTitle)) {
            taskType = TASK_ADD;
        } else {
            taskType = TASK_EDIT;
        }
    }


    public abstract void save(String newTitle);


    void validateInput(String newTitle) {
        if (emptyInput(newTitle)) {
            showError(getStringResource(R.string.error_empty_title_text_field));
        }  else if (titleUnchanged(newTitle)) {
            showError(getStringResource(R.string.error_title_unchanged));
        } else {
            save(newTitle);
            eventDismiss.call();
        }
    }

    private boolean titleUnchanged(String newTitle) {
        return newTitle.equals(currentTitle);
    }

    private void showError(String errorMsg) {
        eventErrorMessage.setValue(errorMsg);
    }

    private boolean emptyInput(String msg) {
        return TextUtils.isEmpty(msg);
    }


    LiveData<String> getEventErrorMessage() {
        return eventErrorMessage;
    }

    LiveData<Void> getEventDismiss() {
        return eventDismiss;
    }


    protected int getTaskType() {
        return taskType;
    }

    private String getStringResource(int resId) {
        return getApplication().getString(resId);
    }
}

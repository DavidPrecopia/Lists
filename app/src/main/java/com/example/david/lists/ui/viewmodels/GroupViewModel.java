package com.example.david.lists.ui.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.adapaters.IGroupAdapterContract;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilNightMode;
import com.example.david.lists.util.UtilUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public final class GroupViewModel extends AndroidViewModel
        implements IGroupViewModelContract {

    private final MutableLiveData<List<Group>> groupList;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final SingleLiveEvent<Group> eventOpenGroup;
    private final SingleLiveEvent<Boolean> eventDisplayError;
    private final SingleLiveEvent<String> errorMessage;
    private final SingleLiveEvent<String> eventNotifyUserOfDeletion;
    private final SingleLiveEvent<String> eventAdd;
    private final SingleLiveEvent<EditingInfo> eventEdit;

    private final SingleLiveEvent<Void> eventSignOut;
    private final SingleLiveEvent<Void> eventSignIn;

    private final List<Group> tempGroups;
    private int tempGroupPosition;

    GroupViewModel(@NonNull Application application, IModelContract model) {
        super(application);
        groupList = new MutableLiveData<>();
        this.model = model;
        disposable = new CompositeDisposable();
        eventOpenGroup = new SingleLiveEvent<>();
        eventDisplayLoading = new MutableLiveData<>();
        eventDisplayError = new SingleLiveEvent<>();
        errorMessage = new SingleLiveEvent<>();
        eventNotifyUserOfDeletion = new SingleLiveEvent<>();
        eventAdd = new SingleLiveEvent<>();
        eventEdit = new SingleLiveEvent<>();
        eventSignOut = new SingleLiveEvent<>();
        eventSignIn = new SingleLiveEvent<>();
        this.tempGroups = new ArrayList<>();
        this.tempGroupPosition = -1;
        init();
    }

    private void init() {
        eventDisplayLoading.setValue(true);
        getAllGroups();
    }


    private void getAllGroups() {
        disposable.add(model.getAllGroups()
                .subscribeWith(groupsSubscriber())
        );
    }

    private DisposableSubscriber<List<Group>> groupsSubscriber() {
        return new DisposableSubscriber<List<Group>>() {
            @Override
            public void onNext(List<Group> groups) {
                GroupViewModel.this.groupList.setValue(groups);
                evaluateNewData(groups);
            }

            @SuppressLint("LogNotTimber")
            @Override
            public void onError(Throwable t) {
                UtilExceptions.throwException(t);
                errorMessage.setValue(getStringResource(R.string.error_msg_generic));
                eventDisplayError.setValue(true);
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private void evaluateNewData(List<Group> newGroupList) {
        eventDisplayLoading.setValue(false);

        if (newGroupList.isEmpty()) {
            errorMessage.setValue(getStringResource(R.string.error_msg_no_groups));
            eventDisplayError.setValue(true);
        } else {
            eventDisplayError.setValue(false);
        }
    }


    @Override
    public void groupClicked(Group group) {
        eventOpenGroup.setValue(group);
    }

    @Override
    public void addButtonClicked() {
        eventAdd.setValue(getStringResource(R.string.hint_add_group));
    }

    @Override
    public void add(String title) {
        model.addGroup(new Group(title, this.groupList.getValue().size()));
    }


    @Override
    public void edit(int position) {
        eventEdit.setValue(new EditingInfo(groupList.getValue().get(position)));
    }

    @Override
    public void changeTitle(EditingInfo editingInfo, String newTitle) {
        model.renameGroup(editingInfo.getId(), newTitle);
    }

    @Override
    public void dragging(IGroupAdapterContract adapter, int fromPosition, int toPosition) {
        adapter.move(fromPosition, toPosition);
        Collections.swap(groupList.getValue(), fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        if (newPosition < 0) {
            return;
        }

        Group group = groupList.getValue().get(newPosition);
        model.updateGroupPosition(
                group,
                group.getPosition(),
                newPosition
        );
    }

    @Override
    public void swipedLeft(IGroupAdapterContract adapter, int position) {
        delete(adapter, position);
    }


    @Override
    public void delete(IGroupAdapterContract adapter, int position) {
        adapter.remove(position);
        tempGroups.add(groupList.getValue().get(position));
        tempGroupPosition = position;

        eventNotifyUserOfDeletion.setValue(
                getStringResource(R.string.message_group_deletion)
        );
    }


    @Override
    public void undoRecentDeletion(IGroupAdapterContract adapter) {
        if (tempGroups.isEmpty() || tempGroupPosition < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    getStringResource(R.string.error_invalid_action_undo_deletion)
            ));
        }
        reAdd(adapter);
        deletionNotificationTimedOut();
    }

    private void reAdd(IGroupAdapterContract adapter) {
        int lastDeletedPosition = tempGroups.size() - 1;
        adapter.reAdd(
                tempGroupPosition,
                tempGroups.get(lastDeletedPosition)
        );
        tempGroups.remove(lastDeletedPosition);
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (tempGroups.isEmpty()) {
            return;
        }
        model.deleteGroups(tempGroups);
        tempGroups.clear();
    }


    @Override
    public void nightMode(MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);
            UtilNightMode.setDay();
            setNightModePreference(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            item.setChecked(true);
            UtilNightMode.setNight();
            setNightModePreference(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void setNightModePreference(int mode) {
        SharedPreferences.Editor editor
                = getApplication().getSharedPreferences(getStringResource(R.string.night_mode_shared_pref_name), Context.MODE_PRIVATE).edit();
        editor.putInt(getStringResource(R.string.night_mode_shared_pref_key), mode);
        editor.apply();
    }

    @Override
    public void signIn() {
        if (UtilUser.isAnonymous()) {
            eventSignIn.call();
        } else {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    getStringResource(R.string.error_sign_in_when_not_anonymous)
            ));
        }
    }

    @Override
    public void signOut() {
        eventSignOut.call();
    }


    @Override
    public LiveData<List<Group>> getGroupList() {
        List<Group> value = groupList.getValue();
        if (value != null) {
            evaluateNewData(value);
        }
        return groupList;
    }

    @Override
    public LiveData<Group> getEventOpenGroup() {
        return eventOpenGroup;
    }

    @Override
    public LiveData<Boolean> getEventDisplayLoading() {
        return eventDisplayLoading;
    }

    @Override
    public LiveData<Boolean> getEventDisplayError() {
        return eventDisplayError;
    }

    @Override
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    public LiveData<String> getEventNotifyUserOfDeletion() {
        return eventNotifyUserOfDeletion;
    }

    @Override
    public LiveData<String> getEventAdd() {
        return eventAdd;
    }

    @Override
    public LiveData<EditingInfo> getEventEdit() {
        return eventEdit;
    }

    @Override
    public LiveData<Void> getEventSignOut() {
        return eventSignOut;
    }

    @Override
    public LiveData<Void> getEventSignIn() {
        return eventSignIn;
    }


    private String getStringResource(int resId) {
        return getApplication().getString(resId);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
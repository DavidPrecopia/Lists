package com.example.david.lists.ui.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.adapaters.IUserListAdapterContract;
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

public final class UserListViewModel extends AndroidViewModel
        implements IUserListViewModelContract {

    private final MutableLiveData<List<UserList>> userLists;

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final SingleLiveEvent<UserList> eventOpenUserList;
    private final SingleLiveEvent<Boolean> eventDisplayError;
    private final SingleLiveEvent<String> errorMessage;
    private final SingleLiveEvent<String> eventNotifyUserOfDeletion;
    private final SingleLiveEvent<String> eventAdd;
    private final SingleLiveEvent<EditingInfo> eventEdit;

    private final SingleLiveEvent<Void> eventSignOut;
    private final SingleLiveEvent<Void> eventSignIn;

    private final List<UserList> tempUserLists;
    private int tempUserListPosition;

    UserListViewModel(@NonNull Application application, IModelContract model) {
        super(application);
        userLists = new MutableLiveData<>();
        this.model = model;
        disposable = new CompositeDisposable();
        eventOpenUserList = new SingleLiveEvent<>();
        eventDisplayLoading = new MutableLiveData<>();
        eventDisplayError = new SingleLiveEvent<>();
        errorMessage = new SingleLiveEvent<>();
        eventNotifyUserOfDeletion = new SingleLiveEvent<>();
        eventAdd = new SingleLiveEvent<>();
        eventEdit = new SingleLiveEvent<>();
        eventSignOut = new SingleLiveEvent<>();
        eventSignIn = new SingleLiveEvent<>();
        this.tempUserLists = new ArrayList<>();
        this.tempUserListPosition = -1;
        init();
    }

    private void init() {
        eventDisplayLoading.setValue(true);
        getAllUserLists();
    }


    private void getAllUserLists() {
        disposable.add(model.getAllUserLists()
                .subscribeWith(userListsSubscriber())
        );
    }

    private DisposableSubscriber<List<UserList>> userListsSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> userLists) {
                UserListViewModel.this.userLists.setValue(userLists);
                evaluateNewData(userLists);
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

    private void evaluateNewData(List<UserList> newUserListList) {
        eventDisplayLoading.setValue(false);

        if (newUserListList.isEmpty()) {
            errorMessage.setValue(getStringResource(R.string.error_msg_no_user_lists));
            eventDisplayError.setValue(true);
        } else {
            eventDisplayError.setValue(false);
        }
    }


    @Override
    public void userListClicked(UserList userList) {
        eventOpenUserList.setValue(userList);
    }

    @Override
    public void addButtonClicked() {
        eventAdd.setValue(getStringResource(R.string.hint_add_user_list));
    }

    @Override
    public void add(String title) {
        model.addUserList(new UserList(title, this.userLists.getValue().size()));
    }


    @Override
    public void edit(int position) {
        eventEdit.setValue(new EditingInfo(userLists.getValue().get(position)));
    }

    @Override
    public void changeTitle(EditingInfo editingInfo, String newTitle) {
        model.renameUserList(editingInfo.getId(), newTitle);
    }

    @Override
    public void dragging(IUserListAdapterContract adapter, int fromPosition, int toPosition) {
        adapter.move(fromPosition, toPosition);
        Collections.swap(userLists.getValue(), fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        if (newPosition < 0) {
            return;
        }

        UserList userList = userLists.getValue().get(newPosition);
        model.updateUserListPosition(
                userList,
                userList.getPosition(),
                newPosition
        );
    }

    @Override
    public void swipedLeft(IUserListAdapterContract adapter, int position) {
        delete(adapter, position);
    }


    @Override
    public void delete(IUserListAdapterContract adapter, int position) {
        adapter.remove(position);
        tempUserLists.add(userLists.getValue().get(position));
        tempUserListPosition = position;

        eventNotifyUserOfDeletion.setValue(
                getStringResource(R.string.message_user_list_deletion)
        );
    }


    @Override
    public void undoRecentDeletion(IUserListAdapterContract adapter) {
        if (tempUserLists.isEmpty() || tempUserListPosition < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    getStringResource(R.string.error_invalid_action_undo_deletion)
            ));
        }
        reAdd(adapter);
        deletionNotificationTimedOut();
    }

    private void reAdd(IUserListAdapterContract adapter) {
        int lastDeletedPosition = tempUserLists.size() - 1;
        adapter.reAdd(
                tempUserListPosition,
                tempUserLists.get(lastDeletedPosition)
        );
        tempUserLists.remove(lastDeletedPosition);
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (tempUserLists.isEmpty()) {
            return;
        }
        model.deleteUserLists(tempUserLists);
        tempUserLists.clear();
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
    public LiveData<List<UserList>> getUserLists() {
        List<UserList> value = userLists.getValue();
        if (value != null) {
            evaluateNewData(value);
        }
        return userLists;
    }

    @Override
    public LiveData<UserList> getEventOpenUserList() {
        return eventOpenUserList;
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
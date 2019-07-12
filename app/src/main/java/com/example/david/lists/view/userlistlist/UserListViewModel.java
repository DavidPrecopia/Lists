package com.example.david.lists.view.userlistlist;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilNightMode;
import com.example.david.lists.view.common.ViewModelBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public final class UserListViewModel extends ViewModelBase
        implements IUserListViewContract.ViewModel {

    private final IRepositoryContract.UserRepository userRepository;
    private final MutableLiveData<List<UserList>> userLists;

    private final SingleLiveEvent<UserList> eventOpenUserList;
    private final SingleLiveEvent<Void> eventAdd;
    private final SingleLiveEvent<UserList> eventEdit;

    private final SingleLiveEvent<Void> eventSignOut;
    private final SingleLiveEvent<Void> eventSignIn;

    private final List<UserList> tempUserLists;
    private int tempUserListPosition;

    private final SharedPreferences sharedPrefs;

    public UserListViewModel(@NonNull Application application,
                             IRepositoryContract.Repository repository,
                             IRepositoryContract.UserRepository userRepository,
                             CompositeDisposable disposable,
                             SharedPreferences sharedPrefs) {
        super(application, repository, disposable);
        this.userRepository = userRepository;
        userLists = new MutableLiveData<>();
        eventOpenUserList = new SingleLiveEvent<>();
        eventAdd = new SingleLiveEvent<>();
        eventEdit = new SingleLiveEvent<>();
        eventSignOut = new SingleLiveEvent<>();
        eventSignIn = new SingleLiveEvent<>();
        this.tempUserLists = new ArrayList<>();
        this.tempUserListPosition = -1;
        this.sharedPrefs = sharedPrefs;
        init();
    }

    private void init() {
        getAllUserLists();
    }


    private void getAllUserLists() {
        disposable.add(repository.getAllUserLists()
                .subscribeWith(userListSubscriber())
        );
    }

    private DisposableSubscriber<List<UserList>> userListSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> userLists) {
                UserListViewModel.this.userLists.setValue(userLists);
                evaluateNewData(userLists);
            }

            @Override
            public void onError(Throwable t) {
                errorMessage.setValue(getStringResource(R.string.error_msg_generic));
                eventDisplayError.setValue(true);
            }

            @Override
            public void onComplete() {
            }
        };
    }

    @Override
    protected int getEmptyListErrorMessage() {
        return R.string.error_msg_no_user_lists;
    }


    @Override
    public void userListClicked(UserList userList) {
        eventOpenUserList.setValue(userList);
    }

    @Override
    public void addButtonClicked() {
        eventAdd.call();
    }

    @Override
    public void edit(UserList userList) {
        eventEdit.setValue(userList);
    }


    @Override
    public void dragging(IUserListViewContract.Adapter adapter, int fromPosition, int toPosition) {
        adapter.move(fromPosition, toPosition);
        Collections.swap(userLists.getValue(), fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        if (newPosition < 0) {
            return;
        }

        UserList userList = userLists.getValue().get(newPosition);
        repository.updateUserListPosition(
                userList,
                userList.getPosition(),
                newPosition
        );
    }

    @Override
    public void swipedLeft(IUserListViewContract.Adapter adapter, int position) {
        delete(adapter, position);
    }


    @Override
    public void delete(IUserListViewContract.Adapter adapter, int position) {
        adapter.remove(position);
        saveDeletedUserList(position);
        eventNotifyUserOfDeletion.setValue(
                getStringResource(R.string.message_user_list_deletion)
        );
    }

    private void saveDeletedUserList(int position) {
        tempUserLists.add(userLists.getValue().get(position));
        tempUserListPosition = position;
        userLists.getValue().remove(position);
    }


    @Override
    public void undoRecentDeletion(IUserListViewContract.Adapter adapter) {
        if (tempUserLists.isEmpty() || tempUserListPosition < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    getStringResource(R.string.error_invalid_action_undo_deletion)
            ));
        }
        reAdd(adapter);
        deletionNotificationTimedOut();
    }

    private void reAdd(IUserListViewContract.Adapter adapter) {
        int lastDeletedPosition = (tempUserLists.size() - 1);
        reAddUserListToAdapter(adapter, lastDeletedPosition);
        reAddUserListToLocalList(lastDeletedPosition);
        tempUserLists.remove(lastDeletedPosition);
    }

    private void reAddUserListToAdapter(IUserListViewContract.Adapter adapter, int lastDeletedPosition) {
        adapter.reAdd(tempUserListPosition, tempUserLists.get(lastDeletedPosition));
    }

    private void reAddUserListToLocalList(int lastDeletedPosition) {
        userLists.getValue().add(tempUserListPosition, tempUserLists.get(lastDeletedPosition));
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (tempUserLists.isEmpty()) {
            return;
        }
        repository.deleteUserLists(tempUserLists);
        tempUserLists.clear();
    }


    @Override
    public void onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_id_sign_out:
                signOut();
                break;
            case R.id.menu_id_sign_in:
                signIn();
                break;
            case R.id.menu_id_night_mode:
                nightMode(menuItem);
                break;
            default:
                UtilExceptions.throwException(new IllegalArgumentException());
        }
    }

    @Override
    public void signOut() {
        eventSignOut.call();
    }

    private void signIn() {
        if (userRepository.isAnonymous()) {
            eventSignIn.call();
        } else {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    getStringResource(R.string.error_sign_in_when_not_anonymous)
            ));
        }
    }

    private void nightMode(MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);
            UtilNightMode.setDay(getApplication());
        } else {
            item.setChecked(true);
            UtilNightMode.setNight(getApplication());
        }
    }

    @Override
    public int getMenuResource() {
        return userRepository.isAnonymous()
                ? R.menu.menu_sign_in
                : R.menu.menu_sign_out;
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
    public LiveData<Void> getEventAdd() {
        return eventAdd;
    }

    @Override
    public LiveData<UserList> getEventEdit() {
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


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
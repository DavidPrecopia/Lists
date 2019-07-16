package com.example.david.lists.view.userlistlist;

import android.app.Application;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilNightMode;
import com.example.david.lists.view.addedit.userlist.AddEditUserListDialog;
import com.example.david.lists.view.authentication.AuthView;
import com.example.david.lists.view.authentication.IAuthContract;
import com.example.david.lists.view.itemlist.ItemActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Unlike normal Logic classes, this will hold a reference to the
 * Android framework because it needs access to SharedPrefs and Intents.
 */
public final class UserListLogic implements IUserListViewContract.Logic {

    @NonNull
    private final Application application;

    // Will add ViewModel later
    private final IUserListViewContract.View view;
    private final IUserListViewContract.Adapter adapter;

    private List<UserList> userLists;

    private final IRepositoryContract.Repository repo;
    private final ISchedulerProviderContract schedulerProvider;
    private final CompositeDisposable disposable;

    private final IRepositoryContract.UserRepository userRepo;

    private final List<UserList> tempUserLists;
    private int tempUserListPosition;

    private static final int RESPONSE_CODE = 100;

    public UserListLogic(@NonNull Application application,
                         IUserListViewContract.View view,
                         IUserListViewContract.Adapter adapter, IRepositoryContract.Repository repo,
                         IRepositoryContract.UserRepository userRepo,
                         ISchedulerProviderContract schedulerProvider,
                         CompositeDisposable disposable) {
        this.view = view;
        this.adapter = adapter;
        this.userLists = new ArrayList<>();
        this.application = application;
        this.userRepo = userRepo;
        this.repo = repo;
        this.schedulerProvider = schedulerProvider;
        this.disposable = disposable;

        this.tempUserLists = new ArrayList<>();
        this.tempUserListPosition = -1;
    }


    @Override
    public void onStart() {
        view.setStateLoading();
        getAllUserLists();
    }


    @Override
    public RecyclerView.Adapter getAdapter() {
        return (RecyclerView.Adapter) adapter;
    }


    private void getAllUserLists() {
        disposable.add(repo.getAllUserLists()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(userListSubscriber())
        );
    }

    private DisposableSubscriber<List<UserList>> userListSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> userLists) {
                UserListLogic.this.userLists = userLists;
                evaluateNewData();
            }

            @Override
            public void onError(Throwable t) {
                view.setStateError(getStringRes(R.string.error_msg_generic));
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private void evaluateNewData() {
        adapter.submitList(userLists);

        if (userLists.isEmpty()) {
            view.setStateError(getStringRes(R.string.error_msg_no_user_lists));
        } else {
            view.setStateDisplayList();
        }
    }


    @Override
    public void userListSelected(UserList userList) {
        view.openUserList(getOpenUserListIntent(userList));
    }

    private Intent getOpenUserListIntent(UserList userList) {
        Intent intent = new Intent(application, ItemActivity.class);
        intent.putExtra(getStringRes(R.string.intent_extra_user_list_id), userList.getId());
        intent.putExtra(getStringRes(R.string.intent_extra_user_list_title), userList.getTitle());
        return intent;
    }


    @Override
    public void add() {
        view.openDialog(AddEditUserListDialog.getInstance(
                "", "", userLists.size()
        ));
    }

    @Override
    public void edit(UserList userList) {
        view.openDialog(AddEditUserListDialog.getInstance(
                userList.getId(), userList.getTitle(), userList.getPosition()
        ));
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        adapter.move(fromPosition, toPosition);
        Collections.swap(userLists, fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        if (newPosition < 0) {
            return;
        }

        UserList userList = userLists.get(newPosition);
        repo.updateUserListPosition(
                userList,
                userList.getPosition(),
                newPosition
        );
    }


    @Override
    public void delete(int position) {
        adapter.remove(position);
        saveDeletedUserList(position);
        view.notifyUserOfDeletion(getStringRes(R.string.message_user_list_deletion));
    }

    private void saveDeletedUserList(int position) {
        tempUserLists.add(userLists.get(position));
        tempUserListPosition = position;
        userLists.remove(position);
    }


    @Override
    public void undoRecentDeletion() {
        if (tempUserLists.isEmpty() || tempUserListPosition < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    getStringRes(R.string.error_invalid_action_undo_deletion)
            ));
        }
        reAdd();
        deletionNotificationTimedOut();
    }

    private void reAdd() {
        int lastDeletedPosition = (tempUserLists.size() - 1);
        reAddUserListToAdapter(lastDeletedPosition);
        reAddUserListToLocalList(lastDeletedPosition);
        tempUserLists.remove(lastDeletedPosition);
    }

    private void reAddUserListToAdapter(int lastDeletedPosition) {
        adapter.reAdd(tempUserListPosition, tempUserLists.get(lastDeletedPosition));
    }

    private void reAddUserListToLocalList(int lastDeletedPosition) {
        userLists.add(tempUserListPosition, tempUserLists.get(lastDeletedPosition));
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (tempUserLists.isEmpty()) {
            return;
        }
        repo.deleteUserLists(tempUserLists);
        tempUserLists.clear();
    }


    @Override
    public void signOut() {
        view.openDialog(new ConfirmSignOutDialog());
    }

    @Override
    public void signOutConfirmed() {
        view.openAuthentication(
                getAuthIntent(IAuthContract.AuthGoal.SIGN_OUT),
                RESPONSE_CODE
        );
    }

    @Override
    public void signIn() {
        view.openAuthentication(
                getAuthIntent(IAuthContract.AuthGoal.SIGN_IN),
                RESPONSE_CODE
        );
    }

    private Intent getAuthIntent(IAuthContract.AuthGoal authGoal) {
        Intent intent = new Intent(application, AuthView.class);
        // TODO Move to a ViewModel
        intent.putExtra(getStringRes(R.string.intent_extra_auth), authGoal);
        return intent;
    }


    @Override
    public void authResult(int requestCode, Intent data) {
        if (requestCode != RESPONSE_CODE) {
            return;
        }
        evalAuthResult(data);
    }

    private void evalAuthResult(Intent data) {
        if (authWasSuccessful(data)) {
            view.recreateView();
        }
    }

    private boolean authWasSuccessful(Intent data) {
        return data.getSerializableExtra(getStringRes(R.string.intent_extra_auth_result))
                == IAuthContract.AuthResult.AUTH_SUCCESS;
    }


    @Override
    public void nightMode(MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);
            UtilNightMode.setDay(application);
        } else {
            item.setChecked(true);
            UtilNightMode.setNight(application);
        }
    }

    @Override
    public int getMenuResource() {
        return userRepo.isAnonymous()
                ? R.menu.menu_sign_in
                : R.menu.menu_sign_out;
    }


    private String getStringRes(int resId) {
        return application.getString(resId);
    }


    @Override
    public void onDestroy() {
        disposable.clear();
    }
}
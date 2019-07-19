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
import com.example.david.lists.view.authentication.IAuthContract;

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

    private final IUserListViewContract.View view;
    private final IUserListViewContract.ViewModel viewModel;
    private final IUserListViewContract.Adapter adapter;

    private final IRepositoryContract.Repository repo;
    private final ISchedulerProviderContract schedulerProvider;
    private final CompositeDisposable disposable;

    private final IRepositoryContract.UserRepository userRepo;

    private static final int RESPONSE_CODE = 100;

    public UserListLogic(@NonNull Application application,
                         IUserListViewContract.View view,
                         IUserListViewContract.ViewModel viewModel,
                         IUserListViewContract.Adapter adapter,
                         IRepositoryContract.Repository repo,
                         IRepositoryContract.UserRepository userRepo,
                         ISchedulerProviderContract schedulerProvider,
                         CompositeDisposable disposable) {
        this.view = view;
        this.viewModel = viewModel;
        this.adapter = adapter;
        this.adapter.init(this);
        this.application = application;
        this.userRepo = userRepo;
        this.repo = repo;
        this.schedulerProvider = schedulerProvider;
        this.disposable = disposable;
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
                viewModel.setViewData(userLists);
                evaluateNewData();
            }

            @Override
            public void onError(Throwable t) {
                view.setStateError(viewModel.getErrorMsg());
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private void evaluateNewData() {
        List<UserList> viewData = viewModel.getViewData();
        adapter.submitList(viewData);
        if (viewData.isEmpty()) {
            view.setStateError(viewModel.getErrorMsgEmptyList());
        } else {
            view.setStateDisplayList();
        }
    }


    @Override
    public void userListSelected(int position) {
        view.openUserList(viewModel.getViewData().get(position));
    }


    @Override
    public void add() {
        view.openAddDialog(viewModel.getViewData().size());
    }

    @Override
    public void edit(UserList userList) {
        view.openEditDialog(userList);
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        adapter.move(fromPosition, toPosition);
        Collections.swap(viewModel.getViewData(), fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        if (newPosition < 0) {
            return;
        }

        UserList userList = viewModel.getViewData().get(newPosition);
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
        view.notifyUserOfDeletion(viewModel.getMsgDeletion());
    }

    private void saveDeletedUserList(int position) {
        viewModel.getTempList().add(viewModel.getViewData().get(position));
        viewModel.setTempPosition(position);
        viewModel.getViewData().remove(position);
    }


    @Override
    public void undoRecentDeletion() {
        if (viewModel.getTempList().isEmpty() || viewModel.getTempPosition() < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    viewModel.getMsgInvalidUndo()
            ));
        }
        reAdd();
        deletionNotificationTimedOut();
    }

    private void reAdd() {
        int lastDeletedPosition = (viewModel.getTempList().size() - 1);
        reAddUserListToAdapter(lastDeletedPosition);
        reAddUserListToLocalList(lastDeletedPosition);
        viewModel.getTempList().remove(lastDeletedPosition);
    }

    private void reAddUserListToAdapter(int lastDeletedPosition) {
        adapter.reAdd(
                viewModel.getTempPosition(),
                viewModel.getTempList().get(lastDeletedPosition)
        );
    }

    private void reAddUserListToLocalList(int lastDeletedPosition) {
        viewModel.getViewData().add(
                viewModel.getTempPosition(),
                viewModel.getTempList().get(lastDeletedPosition)
        );
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (viewModel.getTempList().isEmpty()) {
            return;
        }
        repo.deleteUserLists(viewModel.getTempList());
        viewModel.getTempList().clear();
    }


    @Override
    public void signOut() {
        view.confirmSignOut();
    }

    @Override
    public void signOutConfirmed() {
        view.openAuthentication(
                IAuthContract.AuthGoal.SIGN_OUT, RESPONSE_CODE
        );
    }

    @Override
    public void signIn() {
        view.openAuthentication(
                IAuthContract.AuthGoal.SIGN_IN, RESPONSE_CODE
        );
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
        return data.getSerializableExtra(viewModel.getIntentExtraAuthResultKey())
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


    @Override
    public void onDestroy() {
        disposable.clear();
    }
}
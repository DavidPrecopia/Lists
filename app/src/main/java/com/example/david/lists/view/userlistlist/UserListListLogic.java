package com.example.david.lists.view.userlistlist;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.util.IUtilNightModeContract;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.authentication.IAuthContract;
import com.example.david.lists.view.common.ListViewLogicBase;

import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public final class UserListListLogic extends ListViewLogicBase
        implements IUserListViewContract.Logic {

    private final IUserListViewContract.View view;
    private final IUserListViewContract.ViewModel viewModel;

    private final IRepositoryContract.UserRepository userRepo;

    private final IUtilNightModeContract utilNightMode;

    public UserListListLogic(IUserListViewContract.View view,
                             IUserListViewContract.ViewModel viewModel,
                             IRepositoryContract.Repository repo,
                             IRepositoryContract.UserRepository userRepo,
                             ISchedulerProviderContract schedulerProvider,
                             CompositeDisposable disposable,
                             IUtilNightModeContract utilNightMode) {
        super(repo, schedulerProvider, disposable);
        this.view = view;
        this.viewModel = viewModel;
        this.userRepo = userRepo;
        this.utilNightMode = utilNightMode;
    }


    @Override
    public void onStart() {
        view.setStateLoading();
        getAllUserLists();
    }


    private void getAllUserLists() {
        disposable.add(repo.getAllUserLists()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
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
        view.submitList(viewData);
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
    public void dragging(int fromPosition, int toPosition, IUserListViewContract.Adapter adapter) {
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
    public void delete(int position, IUserListViewContract.Adapter adapter) {
        if (position < 0) {
            UtilExceptions.throwException(new IllegalArgumentException());
        }
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
    public void undoRecentDeletion(IUserListViewContract.Adapter adapter) {
        if (viewModel.getTempList().isEmpty() || viewModel.getTempPosition() < 0) {
            UtilExceptions.throwException(new UnsupportedOperationException(
                    viewModel.getMsgInvalidUndo()
            ));
        }
        reAdd(adapter);
        deletionNotificationTimedOut();
    }

    private void reAdd(IUserListViewContract.Adapter adapter) {
        int lastDeletedPosition = (viewModel.getTempList().size() - 1);
        reAddUserListToAdapter(lastDeletedPosition, adapter);
        reAddUserListToLocalList(lastDeletedPosition);
        viewModel.getTempList().remove(lastDeletedPosition);
    }

    private void reAddUserListToAdapter(int lastDeletedPosition, IUserListViewContract.Adapter adapter) {
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
        openAuthView(IAuthContract.AuthGoal.SIGN_OUT);
    }

    @Override
    public void signIn() {
        openAuthView(IAuthContract.AuthGoal.SIGN_IN);
    }

    private void openAuthView(IAuthContract.AuthGoal authGoal) {
        view.openAuthentication(
                authGoal,
                viewModel.getRequestCode(),
                viewModel.getIntentExtraAuthResultKey()
        );
    }


    @Override
    public void authResult(IAuthContract.AuthResult authResult) {
        if (authIsValid(authResult)) {
            view.recreateView();
        }
    }

    private boolean authIsValid(IAuthContract.AuthResult authResult) {
        return authResult == IAuthContract.AuthResult.AUTH_SUCCESS;
    }


    @Override
    public void setNightMode(boolean isMenuItemChecked) {
        if (isMenuItemChecked) {
            utilNightMode.setDay();
        } else {
            utilNightMode.setNight();
        }
    }

    @Override
    public boolean isUserAnon() {
        return userRepo.isAnonymous();
    }

    @Override
    public boolean isNightModeEnabled() {
        return utilNightMode.isNightModeEnabled();
    }


    @Override
    public void onDestroy() {
        disposable.clear();
    }
}
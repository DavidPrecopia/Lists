package com.example.david.lists.view.userlistlist;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.util.UtilNightMode;
import com.example.david.lists.view.addedit.userlist.AddEditUserListDialog;
import com.example.david.lists.view.authentication.ConfirmSignOutDialog;
import com.example.david.lists.view.common.ListViewBase;
import com.example.david.lists.view.userlistlist.buldlogic.DaggerUserListListViewComponent;

import javax.inject.Inject;

public class UserListListView extends ListViewBase
        implements ConfirmSignOutDialog.ConfirmSignOutCallback {


    public interface UserListsFragmentListener {
        int SIGN_OUT = 100;
        int SIGN_IN = 200;

        void authMessage(int message);

        void openUserList(UserList userList);
    }


    @Inject
    IUserListViewContract.ViewModel viewModel;

    @Inject
    IUserListViewContract.Adapter adapter;

    private UserListsFragmentListener userListsFragmentListener;


    public UserListListView() {
    }

    public static UserListListView newInstance() {
        return new UserListListView();
    }


    @Override
    public void onAttach(Context context) {
        inject();
        super.onAttach(context);
        init();
    }

    private void inject() {
        DaggerUserListListViewComponent.builder()
                .application(getActivity().getApplication())
                .fragment(this)
                .movementCallback(this)
                .build()
                .inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void init() {
        this.userListsFragmentListener = (UserListsFragmentListener) getActivity();
        observeViewModel();
    }

    private void observeViewModel() {
        observeUserLists();
        observeEventDisplayError();
        observeEventDisplayLoading();
        observeEventNotifyUserOfDeletion();
        observeEventAdd();
        observeEventEdit();
        observeAccountEvents();
    }

    private void observeUserLists() {
        viewModel.getUserLists().observe(this, userLists -> adapter.submitList(userLists));
    }

    private void observeAccountEvents() {
        viewModel.getEventOpenUserList().observe(this, userList ->
                userListsFragmentListener.openUserList(userList));
        viewModel.getEventSignOut().observe(this, aVoid ->
                userListsFragmentListener.authMessage(UserListsFragmentListener.SIGN_OUT));
        viewModel.getEventSignIn().observe(this, aVoid ->
                userListsFragmentListener.authMessage(UserListsFragmentListener.SIGN_IN));
    }


    private void observeEventDisplayError() {
        viewModel.getEventDisplayError().observe(this, display -> {
            if (display) {
                showError(viewModel.getErrorMessage().getValue());
            } else {
                hideError();
            }
        });
    }

    private void observeEventDisplayLoading() {
        viewModel.getEventDisplayLoading().observe(this, display -> {
            if (display) {
                showLoading();
            } else {
                hideLoading();
            }
        });
    }

    private void observeEventNotifyUserOfDeletion() {
        viewModel.getEventNotifyUserOfDeletion().observe(this, this::notifyDeletionSnackbar);
    }

    private void observeEventAdd() {
        viewModel.getEventAdd().observe(this, aVoid -> openAddDialog());
    }

    private void observeEventEdit() {
        viewModel.getEventEdit().observe(this, this::openEditDialog);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(viewModel.getMenuResource(), menu);
        menu.findItem(R.id.menu_id_night_mode)
                .setChecked(UtilNightMode.isNightModeEnabled(getActivity().getApplication()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModel.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }


    private void openAddDialog() {
        openDialogFragment(
                AddEditUserListDialog.getInstance("", "")
        );
    }

    private void openEditDialog(UserList userList) {
        openDialogFragment(
                AddEditUserListDialog.getInstance(userList.getId(), userList.getTitle())
        );
    }


    @Override
    public void proceedWithSignOut() {
        viewModel.signOut();
    }


    @Override
    protected void addButtonClicked() {
        viewModel.addButtonClicked();
    }

    @Override
    protected void undoRecentDeletion() {
        viewModel.undoRecentDeletion(adapter);
    }

    @Override
    protected void deletionNotificationTimedOut() {
        viewModel.deletionNotificationTimedOut();
    }

    @Override
    protected void draggingListItem(int fromPosition, int toPosition) {
        viewModel.dragging(adapter, fromPosition, toPosition);
    }

    @Override
    protected void permanentlyMoved(int newPosition) {
        viewModel.movedPermanently(newPosition);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected boolean enableUpNavigationOnToolbar() {
        return false;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return (RecyclerView.Adapter) adapter;
    }
}
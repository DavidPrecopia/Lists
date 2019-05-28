package com.example.david.lists.view.userlistlist;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.di.view.userlistlist.DaggerUserListFragmentComponent;
import com.example.david.lists.view.addedit.userlist.AddEditUserListDialogFragment;
import com.example.david.lists.view.authentication.ConfirmSignOutDialogFragment;
import com.example.david.lists.view.common.FragmentBase;

import javax.inject.Inject;

public class UserListFragment extends FragmentBase
        implements ConfirmSignOutDialogFragment.ConfirmSignOutCallback {


    public interface UserListsFragmentListener {
        int SIGN_OUT = 100;
        int SIGN_IN = 200;

        void messages(int message);

        void openUserList(UserList userList);
    }


    @Inject
    IUserListViewModel viewModel;

    @Inject
    IUserListAdapter adapter;

    private UserListsFragmentListener userListsFragmentListener;


    public UserListFragment() {
    }

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }


    @Override
    public void onAttach(Context context) {
        inject();
        super.onAttach(context);
        init();
    }

    private void inject() {
        DaggerUserListFragmentComponent.builder()
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
                userListsFragmentListener.messages(UserListsFragmentListener.SIGN_OUT));
        viewModel.getEventConfirmSignOut().observe(this, aVoid ->
                openDialogFragment(new ConfirmSignOutDialogFragment()));
        viewModel.getEventSignIn().observe(this, aVoid ->
                userListsFragmentListener.messages(UserListsFragmentListener.SIGN_IN));
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
        menu.findItem(R.id.menu_id_night_mode).setChecked(viewModel.isNightModeEnabled());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModel.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }


    private void openAddDialog() {
        openDialogFragment(
                AddEditUserListDialogFragment.getInstance("", "")
        );
    }

    private void openEditDialog(UserList userList) {
        openDialogFragment(
                AddEditUserListDialogFragment.getInstance(userList.getId(), userList.getTitle())
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

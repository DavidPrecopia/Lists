package com.example.david.lists.view.userlistlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilNightMode;
import com.example.david.lists.view.addedit.userlist.AddEditUserListDialog;
import com.example.david.lists.view.authentication.AuthView;
import com.example.david.lists.view.authentication.IAuthContract;
import com.example.david.lists.view.common.ListViewBase;
import com.example.david.lists.view.itemlist.ItemActivity;
import com.example.david.lists.view.userlistlist.buldlogic.DaggerUserListListViewComponent;

import javax.inject.Inject;

public class UserListListView extends ListViewBase
        implements IUserListViewContract.View,
        ConfirmSignOutDialog.ConfirmSignOutCallback {

    @Inject
    IUserListViewContract.Logic logic;

    public UserListListView() {
    }

    public static UserListListView newInstance() {
        return new UserListListView();
    }


    @Override
    public void onAttach(Context context) {
        inject();
        super.onAttach(context);
    }

    private void inject() {
        DaggerUserListListViewComponent.builder()
                .application(getActivity().getApplication())
                .view(this)
                .movementCallback(this)
                .build()
                .inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        logic.onStart();
        return view;
    }

    @Override
    public void onDestroy() {
        logic.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(logic.getMenuResource(), menu);
        menu.findItem(R.id.menu_id_night_mode)
                .setChecked(UtilNightMode.isNightModeEnabled(getActivity().getApplication()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_id_sign_out:
                logic.signOut();
                break;
            case R.id.menu_id_sign_in:
                logic.signIn();
                break;
            case R.id.menu_id_night_mode:
                logic.nightMode(item);
                break;
            default:
                UtilExceptions.throwException(new IllegalArgumentException());
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void openUserList(UserList userList) {
        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtra(getString(R.string.intent_extra_user_list_id), userList.getId());
        intent.putExtra(getString(R.string.intent_extra_user_list_title), userList.getTitle());
        startActivity(intent);
    }


    @Override
    public void confirmSignOut() {
        openDialogFragment(new ConfirmSignOutDialog());
    }

    @Override
    public void signOutConfirmed() {
        logic.signOutConfirmed();
    }

    @Override
    public void openAuthentication(IAuthContract.AuthGoal authGoal, int requestCode) {
        Intent intent = new Intent(getActivity(), AuthView.class);
        intent.putExtra(getString(R.string.intent_extra_auth), authGoal);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logic.authResult(requestCode, data);
    }


    @Override
    public void openAddDialog(int position) {
        openDialogFragment(AddEditUserListDialog.getInstance(
                "", "", position
        ));
    }

    @Override
    public void openEditDialog(UserList userList) {
        openDialogFragment(AddEditUserListDialog.getInstance(
                userList.getId(), userList.getTitle(), userList.getPosition()
        ));
    }


    @Override
    public void notifyUserOfDeletion(String message) {
        notifyDeletionSnackbar(message);
    }


    @Override
    public void setStateDisplayList() {
        displayList();
    }

    @Override
    public void setStateLoading() {
        displayLoading();
    }

    @Override
    public void setStateError(String message) {
        displayError(message);
    }

    @Override
    public void recreateView() {
        getActivity().recreate();
    }


    @Override
    protected void addButtonClicked() {
        logic.add();
    }

    @Override
    protected void undoRecentDeletion() {
        logic.undoRecentDeletion();
    }

    @Override
    protected void deletionNotificationTimedOut() {
        logic.deletionNotificationTimedOut();
    }

    @Override
    protected void draggingListItem(int fromPosition, int toPosition) {
        logic.dragging(fromPosition, toPosition);
    }

    @Override
    protected void permanentlyMoved(int newPosition) {
        logic.movedPermanently(newPosition);
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
        return logic.getAdapter();
    }
}

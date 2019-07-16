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
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilNightMode;
import com.example.david.lists.view.common.ListViewBase;
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
    public void openUserList(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void signOutConfirmed() {
        logic.signOutConfirmed();
    }

    @Override
    public void openAuthentication(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logic.authResult(requestCode, data);
    }

    @Override
    public void openDialog(DialogFragment dialog) {
        openDialogFragment(dialog);
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

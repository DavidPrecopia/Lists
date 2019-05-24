package com.example.david.lists.ui.userlistlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.databinding.FragmentUserListBinding;
import com.example.david.lists.di.view.userlistfragment.DaggerUserListFragmentComponent;
import com.example.david.lists.ui.ConfirmSignOutDialogFragment;
import com.example.david.lists.ui.addedit.userlist.AddEditUserListFragment;
import com.example.david.lists.ui.common.TouchHelperCallback;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;
import javax.inject.Provider;

public class UserListsFragment extends Fragment
        implements TouchHelperCallback.MovementCallback,
        ConfirmSignOutDialogFragment.ConfirmSignOutCallback {


    public interface UserListsFragmentListener {
        int SIGN_OUT = 100;
        int SIGN_IN = 200;

        void messages(int message);

        void openUserList(UserList userList);
    }

    private FragmentUserListBinding binding;

    @Inject
    IUserListViewModel viewModel;

    @Inject
    IUserListAdapter adapter;
    @Inject
    Provider<LinearLayoutManager> layoutManger;
    @Inject
    Provider<RecyclerView.ItemDecoration> dividerItemDecorator;
    @Inject
    Provider<ItemTouchHelper> itemTouchHelper;

    @Inject
    SharedPreferences sharedPrefs;

    private UserListsFragmentListener userListsFragmentListener;


    public UserListsFragment() {
    }

    public static UserListsFragment newInstance() {
        return new UserListsFragment();
    }


    @Override
    public void onAttach(Context context) {
        inject();
        super.onAttach(context);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        this.userListsFragmentListener = (UserListsFragmentListener) getActivity();
        initRecyclerView();
        observeViewModel();
        initToolbar();
        initFab();
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


    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManger.get());
        recyclerView.addItemDecoration(dividerItemDecorator.get());
        itemTouchHelper.get().attachToRecyclerView(recyclerView);
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);
    }


    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
    }

    private void initFab() {
        FloatingActionButton fab = binding.fab;
        fabClickListener(fab);
        fabScrollListener(fab);
    }

    private void fabClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view -> viewModel.addButtonClicked());
    }

    private void fabScrollListener(FloatingActionButton fab) {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.hide();
                } else if (dy < 0) {
                    fab.show();
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(getMenuResource(), menu);
        menu.findItem(R.id.menu_id_night_mode).setChecked(isNightModeEnabled());
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean isNightModeEnabled() {
        return AppCompatDelegate.MODE_NIGHT_YES ==
                sharedPrefs.getInt(getString(R.string.night_mode_shared_pref_key), -1);
    }

    private int getMenuResource() {
        return UtilUser.isAnonymous()
                ? R.menu.menu_sign_in
                : R.menu.menu_sign_out;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_id_sign_in:
                viewModel.signIn();
                break;
            case R.id.menu_id_sign_out:
                viewModel.signOutButtonClicked();
                break;
            case R.id.menu_id_night_mode:
                viewModel.nightMode(item);
                break;
            default:
                UtilExceptions.throwException(new IllegalArgumentException());
        }
        return super.onOptionsItemSelected(item);
    }


    private void openAddDialog() {
        openDialogFragment(
                AddEditUserListFragment.getInstance("", "")
        );
    }

    private void openEditDialog(UserList userList) {
        openDialogFragment(
                AddEditUserListFragment.getInstance(userList.getId(), userList.getTitle())
        );
    }


    @Override
    public void proceedWithSignOut() {
        viewModel.signOut();
    }


    private void notifyDeletionSnackbar(String message) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_undo, view -> viewModel.undoRecentDeletion(adapter))
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        if (validSnackbarEvent(event)) {
                            viewModel.deletionNotificationTimedOut();
                        }
                    }
                })
                .show();
    }

    private boolean validSnackbarEvent(int event) {
        return event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                || event == Snackbar.Callback.DISMISS_EVENT_SWIPE
                || event == Snackbar.Callback.DISMISS_EVENT_MANUAL;
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        viewModel.dragging(adapter, fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        viewModel.movedPermanently(newPosition);
    }


    private void openDialogFragment(DialogFragment dialogFragment) {
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }


    private void showLoading() {
        hideError();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.fab.hide();
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.fab.show();
    }

    private void showError(String errorMessage) {
        hideLoading();
        binding.tvError.setText(errorMessage);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        binding.tvError.setVisibility(View.GONE);
    }
}

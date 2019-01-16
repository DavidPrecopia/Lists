package com.example.david.lists.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.databinding.FragmentUserListBinding;
import com.example.david.lists.di.view.DaggerUserListFragmentComponent;
import com.example.david.lists.ui.adapaters.TouchHelperCallback;
import com.example.david.lists.ui.adapaters.UserListsAdapter;
import com.example.david.lists.ui.viewmodels.IUserListViewModelContract;
import com.example.david.lists.ui.viewmodels.UserListViewModel;
import com.example.david.lists.ui.viewmodels.UserListViewModelFactory;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserListsFragment extends Fragment
        implements AddDialogFragment.AddDialogFragmentListener,
        EditDialogFragment.EditDialogFragmentListener,
        TouchHelperCallback.MovementCallback,
        ConfirmSignOutDialogFragment.ConfirmSignOutCallback {


    interface UserListsFragmentListener {
        int SIGN_OUT = 100;
        int SIGN_IN = 200;

        void messages(int message);

        void openGroup(UserList userList);
    }


    private IUserListViewModelContract viewModel;
    @Inject
    UserListViewModelFactory viewModelFactory;

    private FragmentUserListBinding binding;
    private UserListsAdapter adapter;

    private UserListsFragmentListener userListsFragmentListener;


    public UserListsFragment() {
    }

    static UserListsFragment newInstance() {
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
                .build()
                .inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intiViewModel();
        setHasOptionsMenu(true);
    }

    private void intiViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserListViewModel.class);
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
        observeGroupList();
        observeEventDisplayError();
        observeEventDisplayLoading();
        observeEventNotifyUserOfDeletion();
        observeEventAdd();
        observeEventEdit();
        observeAccountEvents();
    }

    private void observeGroupList() {
        viewModel.getUserLists().observe(this, groups -> adapter.swapData(groups));
    }

    private void observeAccountEvents() {
        viewModel.getEventOpenUserList().observe(this, group ->
                userListsFragmentListener.openGroup(group));
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
        viewModel.getEventAdd().observe(this, this::openAddDialog);
    }

    private void observeEventEdit() {
        viewModel.getEventEdit().observe(this, this::openEditDialog);
    }


    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        initLayoutManager(recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        this.adapter = new UserListsAdapter(viewModel, itemTouchHelper);
        recyclerView.setAdapter(adapter);
    }

    private void initLayoutManager(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerDecorator(recyclerView, layoutManager));
    }

    private DividerItemDecoration getDividerDecorator(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
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
        menu.findItem(R.id.menu_id_night_mode).setChecked(nightModeEnabled());
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean nightModeEnabled() {
        return AppCompatDelegate.MODE_NIGHT_YES == getActivity()
                .getSharedPreferences(getString(R.string.night_mode_shared_pref_name), Context.MODE_PRIVATE)
                .getInt(getString(R.string.night_mode_shared_pref_key), -1);
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


    private void openAddDialog(String hintMessage) {
        openDialogFragment(
                AddDialogFragment.getInstance(hintMessage)
        );
    }

    @Override
    public void add(String title) {
        viewModel.add(title);
    }


    private void openEditDialog(EditingInfo editingInfo) {
        openDialogFragment(
                EditDialogFragment.getInstance(editingInfo)
        );
    }

    @Override
    public void edit(EditingInfo editingInfo, String newTitle) {
        viewModel.changeTitle(editingInfo, newTitle);
    }

    @Override
    public void proceedWithSignOut() {
        viewModel.signOut();
    }


    private void notifyDeletionSnackbar(String message) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_SHORT)
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

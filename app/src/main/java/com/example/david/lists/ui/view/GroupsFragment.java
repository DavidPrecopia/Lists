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
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.databinding.FragmentGroupsBinding;
import com.example.david.lists.ui.adapaters.GroupAdapter;
import com.example.david.lists.ui.adapaters.TouchHelperCallback;
import com.example.david.lists.ui.viewmodels.GroupViewModel;
import com.example.david.lists.ui.viewmodels.GroupViewModelFactory;
import com.example.david.lists.ui.viewmodels.IGroupViewModelContract;
import com.example.david.lists.util.UtilUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

public class GroupsFragment extends Fragment
        implements AddDialogFragment.AddDialogFragmentListener,
        EditDialogFragment.EditDialogFragmentListener, TouchHelperCallback.MovementCallback {


    interface GroupFragmentListener {
        int SIGN_OUT = 100;
        int SIGN_IN = 200;

        void messages(int message);

        void openGroup(Group group);
    }


    private IGroupViewModelContract viewModel;
    private FragmentGroupsBinding binding;
    private GroupAdapter adapter;

    private GroupFragmentListener groupFragmentListener;


    public GroupsFragment() {
    }

    static GroupsFragment newInstance() {
        return new GroupsFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        intiViewModel();
    }

    private void intiViewModel() {
        GroupViewModelFactory factory = new GroupViewModelFactory(getActivity().getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(GroupViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_groups, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        this.groupFragmentListener = (GroupFragmentListener) getActivity();
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
        viewModel.getGroupList().observe(this, groups -> adapter.swapData(groups));
    }

    private void observeAccountEvents() {
        viewModel.getEventOpenGroup().observe(this, group ->
                groupFragmentListener.openGroup(group));
        viewModel.getEventSignOut().observe(this, aVoid ->
                groupFragmentListener.messages(GroupFragmentListener.SIGN_OUT));
        viewModel.getEventSignIn().observe(this, aVoid ->
                groupFragmentListener.messages(GroupFragmentListener.SIGN_IN));
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
        this.adapter = new GroupAdapter(viewModel, itemTouchHelper);
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
                viewModel.signOut();
                break;
            case R.id.menu_id_night_mode:
                viewModel.nightMode(item);
                break;
            default:
                throw new IllegalArgumentException();
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


    private void notifyDeletionSnackbar(String message) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_undo, view -> viewModel.undoRecentDeletion(adapter))
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        // If it was replaced by another Snackbar, do not forward.
                        if (event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                            viewModel.deletionNotificationTimedOut();
                        }
                    }
                })
                .show();
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        viewModel.dragging(adapter, fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        viewModel.movedPermanently(newPosition);
    }

    @Override
    public void swipedLeft(int position) {
        viewModel.swipedLeft(adapter, position);
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
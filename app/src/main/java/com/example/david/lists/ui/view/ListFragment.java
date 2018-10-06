package com.example.david.lists.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentListBinding;
import com.example.david.lists.ui.dialogs.AddDialogFragment;
import com.example.david.lists.ui.dialogs.EditDialogFragment;
import com.example.david.lists.ui.dialogs.EditingInfo;
import com.example.david.lists.ui.viewmodels.IListViewModelContract;
import com.example.david.lists.ui.viewmodels.UtilViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import timber.log.Timber;

public class ListFragment extends Fragment
        implements AddDialogFragment.AddDialogFragmentListener,
        EditDialogFragment.EditDialogFragmentListener {

    private IListViewModelContract viewModel;
    private FragmentListBinding binding;

    private static final String ARG_KEY_DISPLAYING = "displaying_key";

    public ListFragment() {
    }

    static ListFragment newInstance(String displaying) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_DISPLAYING, displaying);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initViewModel();
    }

    private void initViewModel() {
        String currentlyDisplaying = getArguments().getString(ARG_KEY_DISPLAYING);
        if (currentlyDisplaying.equals(getStringResource(R.string.displaying_user_list))) {
            viewModel = UtilViewModel.getUserListViewModel(
                    (ListActivity) getActivity(),
                    getActivity().getApplication()
            );
        } else if (currentlyDisplaying.equals(getStringResource(R.string.displaying_item))) {
            viewModel = UtilViewModel.getItemViewModel(
                    this,
                    getActivity().getApplication()
            );
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        observeViewModel();
        initRecyclerView();
        initToolbar();
        initFab();
        initSwipeRefresh();
    }

    private void observeViewModel() {
        observeToolbarTitle();
        observeDisplayLoading();
        observeError();
        observeEventNotifyUserOfDeletion();
        observeEventAdd();
        observeEventEdit();
    }

    private void observeToolbarTitle() {
        viewModel.getToolbarTitle().observe(this, title -> binding.toolbar.setTitle(title));
    }

    private void observeDisplayLoading() {
        viewModel.getEventDisplayLoading().observe(this, display -> {
            if (display) {
                showLoading();
            } else {
                hideLoading();
            }
        });
    }

    private void observeError() {
        viewModel.getEventDisplayError().observe(this, this::showError);
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
        new ItemTouchHelper(getItemTouchCallback()).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(viewModel.getAdapter());
    }

    private void initLayoutManager(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplication());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerDecorator(recyclerView, layoutManager));
    }

    private DividerItemDecoration getDividerDecorator(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
    }

    private ItemTouchHelper.SimpleCallback getItemTouchCallback() {
        return new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                viewModel.dragging(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewModel.movePermanently(viewHolder.getAdapterPosition());
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        viewModel.swipedLeft(position);
                        break;
                    case ItemTouchHelper.RIGHT:
                        viewModel.swipedRight(position);
                        break;
                }
            }
        };
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
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


    private void initSwipeRefresh() {
        SwipeRefreshLayout swipeRefresh = binding.swipeRefreshLayout;
        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refresh();
            swipeRefresh.setRefreshing(false);
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_log_out, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_id_log_out:
                Timber.i("Log out");
                break;
            case R.id.menu_id_log_in:
                Timber.i("Log in");
                break;
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
    public void edit(int id, String newTitle) {
        viewModel.changeTitle(id, newTitle);
    }


    private void notifyDeletionSnackbar(String message) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_undo, view -> viewModel.undoRecentDeletion())
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION && event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                            viewModel.deletionNotificationTimedOut();
                        }
                    }
                })
                .show();
    }


    private void openDialogFragment(DialogFragment dialogFragment) {
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }


    private void showLoading() {
        hideError();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setVisibility(View.GONE);
        binding.fab.hide();
    }

    private void hideLoading() {
        hideError();
        binding.progressBar.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
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


    private String getStringResource(int resId) {
        return getActivity().getApplication().getString(resId);
    }
}

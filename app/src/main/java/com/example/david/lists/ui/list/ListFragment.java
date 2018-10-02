package com.example.david.lists.ui.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentListSharedBinding;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.ui.UtilInitRecyclerView;
import com.example.david.lists.ui.dialogs.AddDialogFragment;
import com.example.david.lists.ui.dialogs.EditDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import timber.log.Timber;

public class ListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        AddDialogFragment.AddDialogFragmentListener,
        EditDialogFragment.EditDialogFragmentListener {

    private ListViewModel viewModel;
    private FragmentListSharedBinding binding;

    private UserListsAdapter adapter;

    private ListFragmentClickListener fragmentClickListener;

    public ListFragment() {
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initViewModel();
    }

    private void initViewModel() {
        ListViewModelFactory factory = new ListViewModelFactory(getActivity().getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(ListViewModel.class);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentClickListener = (ListFragmentClickListener) context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_shared, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        showLoading();
        observeViewModel();
        initRecyclerView();
        initToolbar();
        initFab();
        initSwipeRefresh();
    }

    private void observeViewModel() {
        observerUserLists();
        observerError();
    }

    private void observerUserLists() {
        viewModel.getUserLists().observe(this, userLists -> {
            hideLoading();
            if (userLists == null || userLists.isEmpty()) {
                showError(getString(R.string.error_msg_no_lists));
            } else {
                adapter.swapData(userLists);
            }
        });
    }

    private void observerError() {
        viewModel.getError().observe(this, this::showError);
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getContext().getString(R.string.app_name));
    }

    private void initRecyclerView() {
        UtilInitRecyclerView.initRecyclerView(
                binding.recyclerView,
                getRecyclerViewAdapter(),
                getItemTouchCallback(),
                getActivity().getApplication()
        );
    }

    private ItemTouchHelper.SimpleCallback getItemTouchCallback() {
        return new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        viewModel.prepareToDelete(adapter.getData(position));
                        adapter.removeUserList(position);
                        notifyDeletionSnackbar();
                        break;
                    case ItemTouchHelper.RIGHT:
                        UserList userList = adapter.getData(position);
                        dialogFragmentCommonSteps(
                                EditDialogFragment.getInstance(userList.getId(), userList.getTitle())
                        );
                        break;
                }
            }

            private void notifyDeletionSnackbar() {
                Snackbar.make(binding.coordinatorLayout, R.string.message_list_deletion, Snackbar.LENGTH_LONG)
                        .setAction(R.string.message_undo, view -> adapter.swapData(viewModel.undoDeletion()))
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION && event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                                    viewModel.permanentlyDelete();
                                }
                            }
                        })
                        .show();
            }
        };
    }

    private RecyclerView.Adapter getRecyclerViewAdapter() {
        adapter = new UserListsAdapter(fragmentClickListener);
        if (viewModel.getUserLists().getValue() != null) {
            adapter.swapData(viewModel.getUserLists().getValue());
        }
        return adapter;
    }


    private void initFab() {
        FloatingActionButton fab = binding.fab;
        initFabClickListener(fab);
        initFabScrollListener(fab);
    }

    private void initFabClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view ->
                dialogFragmentCommonSteps(
                        AddDialogFragment.getInstance(getString(R.string.hint_add_list))
                )
        );
    }

    private void initFabScrollListener(FloatingActionButton fab) {
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


    private void initSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this);
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


    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void add(String title) {
        // getItemCount returns the length of the list in use,
        // ensuring the new UserList is added at the bottom
        int position = adapter == null ? 0 : adapter.getItemCount();
        viewModel.add(title, position);
    }

    @Override
    public void edit(int id, String newTitle) {
        viewModel.changeTitle(id, newTitle);
    }


    private void dialogFragmentCommonSteps(DialogFragment dialogFragment) {
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        fragmentClickListener = null;
    }


    public interface ListFragmentClickListener {
        void openDetailFragment(int listId, String listTitle);
    }
}

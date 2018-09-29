package com.example.david.lists.ui;

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
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.datamodel.UserList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    static ListFragment newInstance() {
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
                initRecyclerView(userLists);
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

    private void initRecyclerView(List<UserList> userLists) {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerDecorator(recyclerView, layoutManager));
        adapter = new UserListsAdapter(userLists);
        recyclerView.setAdapter(adapter);
    }

    private DividerItemDecoration getDividerDecorator(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
    }


    private void initFab() {
        FloatingActionButton fab = binding.fab;
        initFabClickListener(fab);
        initFabScrollListener(fab);
    }

    private void initFabClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view -> {
            AddDialogFragment dialogFragment = AddDialogFragment.getInstance(getString(R.string.hint_add_list));
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getActivity().getSupportFragmentManager(), null);
        });
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

    private void initSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this);
    }


    private void snackbarMessage(String message) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
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
        snackbarMessage("Swiped Refresh");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        fragmentClickListener = null;
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
                snackbarMessage("Log Out");
                break;
            case R.id.menu_id_log_in:
                snackbarMessage("Log In");
                break;
        }
        return super.onOptionsItemSelected(item);
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


    interface ListFragmentClickListener {
        void openDetailFragment(int listId, String listTitle);
    }


    private final class UserListsAdapter extends RecyclerView.Adapter<UserListsAdapter.UserListViewHolder> {

        private final List<UserList> userLists;

        UserListsAdapter(List<UserList> userLists) {
            this.userLists = userLists;
        }

        @NonNull
        @Override
        public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserListViewHolder(
                    ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull UserListViewHolder userListViewHolder, int position) {
            userListViewHolder.bindView(
                    userLists.get(userListViewHolder.getAdapterPosition())
            );
        }

        @Override
        public int getItemCount() {
            return userLists.size();
        }


        final class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private final ListItemBinding binding;

            UserListViewHolder(@NonNull ListItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.getRoot().setOnClickListener(this);
            }

            void bindView(UserList userList) {
                binding.tvTitle.setText(userList.getTitle());
                binding.executePendingBindings();
            }

            @Override
            public void onClick(View v) {
                UserList userList = userLists.get(getAdapterPosition());
                fragmentClickListener.openDetailFragment(
                        userList.getId(), userList.getTitle()
                );
            }
        }
    }
}

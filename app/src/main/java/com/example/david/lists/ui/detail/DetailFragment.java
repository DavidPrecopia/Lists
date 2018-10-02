package com.example.david.lists.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentListSharedBinding;
import com.example.david.lists.datamodel.Item;
import com.example.david.lists.ui.UtilInitRecyclerView;
import com.example.david.lists.ui.dialogs.AddDialogFragment;
import com.example.david.lists.ui.dialogs.EditDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
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

public class DetailFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        AddDialogFragment.AddDialogFragmentListener,
        EditDialogFragment.EditDialogFragmentListener {

    private DetailViewModel viewModel;
    private FragmentListSharedBinding binding;

    private ItemsAdapter adapter;

    private static final String ARG_KEY_LIST_ID = "list_id_key";
    private static final String ARG_KEY_LIST_NAME = "list_name_key";

    public DetailFragment() {
    }

    public static DetailFragment newInstance(int listId, String listName) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_KEY_LIST_ID, listId);
        bundle.putString(ARG_KEY_LIST_NAME, listName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void initViewModel() {
        DetailViewModelFactory factory = new DetailViewModelFactory(
                getActivity().getApplication(),
                getArguments().getInt(ARG_KEY_LIST_ID)
        );
        viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
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
        observeItemList();
        observerError();
    }

    private void observeItemList() {
        viewModel.getItemList().observe(this, itemList -> {
            hideLoading();
            if (itemList == null || itemList.isEmpty()) {
                showError(getString(R.string.error_msg_empty_list));
            } else {
                adapter.swapData(itemList);
            }
        });
    }

    private void observerError() {
        viewModel.getError().observe(this, this::showError);
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setTitle(getArguments().getString(ARG_KEY_LIST_NAME));
    }


    private void initRecyclerView() {
        UtilInitRecyclerView.initRecyclerView(
                binding.recyclerView,
                getRecyclerViewAdapter(),
                getItemTouchCallback(),
                getActivity().getApplication()
        );
    }

    private RecyclerView.Adapter getRecyclerViewAdapter() {
        adapter = new ItemsAdapter();
        if (viewModel.getItemList().getValue() != null) {
            adapter.swapData(viewModel.getItemList().getValue());
        }
        return adapter;
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
                        Item item = adapter.getData(position);
                        commonDialogFragmentSteps(
                                EditDialogFragment.getInstance(item.getId(), item.getTitle())
                        );
                        break;
                }
            }
        };
    }


    private void initFab() {
        FloatingActionButton fab = binding.fab;
        initFabClickListener(fab);
        initFabScrollListener(fab);
    }

    private void initFabClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view ->
                commonDialogFragmentSteps(
                        AddDialogFragment.getInstance(getString(R.string.hint_add_item))
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
        // ensuring the new Item is added at the bottom
        int position = adapter == null ? 0 : adapter.getItemCount();
        viewModel.add(title, position, viewModel.getListId());
    }

    @Override
    public void edit(int id, String newTitle) {
        viewModel.changeTitle(id, newTitle);
    }


    private void commonDialogFragmentSteps(DialogFragment dialogFragment) {
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }


    private void notifyDeletionSnackbar() {
        Snackbar.make(binding.coordinatorLayout, R.string.message_list_deletion, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_undo, view -> viewModel.undoDeletion())
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        viewModel.permanentlyDelete();
                    }
                })
                .show();
    }
}

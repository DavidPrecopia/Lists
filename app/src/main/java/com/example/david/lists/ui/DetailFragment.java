package com.example.david.lists.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentListSharedBinding;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.datamodel.Item;
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

public class DetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private DetailViewModel viewModel;
    private FragmentListSharedBinding binding;

    private static final String ARG_PARAM_LIST_ID = "list_id_key";

    public DetailFragment() {
    }

    static DetailFragment newInstance(int listId) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PARAM_LIST_ID, listId);
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
                getArguments().getInt(ARG_PARAM_LIST_ID)
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
                initRecyclerView(itemList);
            }
        });
    }

    private void observerError() {
        viewModel.getError().observe(this, this::showError);
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO Title will be list's name - start with blank title - set title in LiveData's observer
        binding.toolbar.setTitle("DETAIL PLACEHOLDER");
    }


    private void initRecyclerView(List<Item> itemList) {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerDecorator(recyclerView, layoutManager));
        recyclerView.setAdapter(new ItemsAdapter(itemList));
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
        fab.setOnClickListener(view -> snackbarMessage("FAB clicked"));
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


    // TODO Implement swipe refresh
    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(false);
        snackbarMessage("Swiped Refresh");
    }


    private final class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {

        private final List<Item> itemsList;

        ItemsAdapter(List<Item> itemsList) {
            this.itemsList = itemsList;
        }

        @NonNull
        @Override
        public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemsViewHolder(
                    ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ItemsViewHolder itemsViewHolder, int position) {
            itemsViewHolder.bindView(
                    itemsList.get(itemsViewHolder.getAdapterPosition())
            );
        }

        @Override
        public int getItemCount() {
            return itemsList.size();
        }


        final class ItemsViewHolder extends RecyclerView.ViewHolder {

            private final ListItemBinding binding;

            ItemsViewHolder(@NonNull ListItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            private void bindView(Item item) {
                binding.tvName.setText(item.getName());
                binding.executePendingBindings();
            }
        }
    }
}

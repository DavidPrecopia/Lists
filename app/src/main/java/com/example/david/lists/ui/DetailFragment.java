package com.example.david.lists.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentListSharedBinding;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentListSharedBinding binding;

    private static final String ARG_PARAM_LIST_ID = "list_id_key";

    public DetailFragment() {
    }

    public static DetailFragment newInstance(int listId) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PARAM_LIST_ID, listId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO initialize ViewModel
        // getArguments().getInt(ARG_PARAM_LIST_ID)
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_shared, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        initToolbar();
        // TODO Move to LiveData observer
        initRecyclerView();
        initFab();
        initSwipeRefresh();
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO Title will be list's name - start with blank title - add in LiveData's observer
        binding.toolbar.setTitle("DETAIL PLACEHOLDER");
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerDecorator(recyclerView, layoutManager));
        // FOR TESTING PURPOSES
        recyclerView.setAdapter(new ItemsAdapter(getUsersLists()));
    }

    private DividerItemDecoration getDividerDecorator(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
    }

    /**
     * FOR TESTING PURPOSES
     */
    private List<Item> getUsersLists() {
        List<Item> testing = new ArrayList<>();
        for (int x = 5; x < 20; x++) {
            testing.add(new Item(x, String.valueOf(x), x, x));
        }
        return testing;
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

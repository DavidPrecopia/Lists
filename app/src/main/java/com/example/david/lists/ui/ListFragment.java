package com.example.david.lists.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentListBinding;
import com.example.david.lists.util.UtilInitializeListRecyclerView;
import com.example.david.lists.util.ViewModelFactory;
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
import timber.log.Timber;

public class ListFragment extends Fragment {

    private ListViewModel viewModel;
    private FragmentListBinding binding;

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
        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(ListViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        observeViewModel();
        binding.setViewModel(viewModel);
        initToolbar();
        initFab();
        initSwipeRefresh();
    }

    private void observeViewModel() {
        observeRecyclerViewAdapter();
        observeDisplayLoading();
        observeError();
    }

    private void observeDisplayLoading() {
        viewModel.getDisplayLoading().observe(this, display -> {
            if (display) {
                showLoading();
            } else {
                hideLoading();
            }
        });
    }

    private void observeRecyclerViewAdapter() {
        viewModel.getRecyclerViewAdapter().observe(this, this::initRecyclerView);
    }

    private void observeError() {
        viewModel.getError().observe(this, this::showError);
    }

    private void initRecyclerView(RecyclerView.Adapter adapter) {
        UtilInitializeListRecyclerView.initRecyclerView(
                binding.recyclerView,
                adapter,
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
        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refresh());
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


    private void notifyDeletionSnackbar() {
        Snackbar.make(binding.coordinatorLayout, R.string.message_list_deletion, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_undo, view -> viewModel.undoDeletion())
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
}

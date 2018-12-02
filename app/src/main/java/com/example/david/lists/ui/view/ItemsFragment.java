package com.example.david.lists.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.databinding.FragmentItemsBinding;
import com.example.david.lists.ui.adapaters.ItemsAdapter;
import com.example.david.lists.ui.adapaters.TouchHelperCallback;
import com.example.david.lists.ui.viewmodels.IItemViewModelContract;
import com.example.david.lists.ui.viewmodels.UtilListViewModels;
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

public class ItemsFragment extends Fragment
        implements AddDialogFragment.AddDialogFragmentListener,
        EditDialogFragment.EditDialogFragmentListener,
        TouchHelperCallback.MovementCallback {

    private IItemViewModelContract viewModel;
    private FragmentItemsBinding binding;
    private ItemsAdapter adapter;

    private static final String ARG_KEY_GROUP_ID = "group_id_key";
    private static final String ARG_KEY_GROUP_TITLE = "group_title_key";

    public ItemsFragment() {
    }

    static ItemsFragment newInstance(String groupId, String groupTitle) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_GROUP_ID, groupId);
        bundle.putString(ARG_KEY_GROUP_TITLE, groupTitle);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void initViewModel() {
        viewModel = UtilListViewModels.getItemViewModel(
                this,
                getActivity().getApplication(),
                getArguments().getString(ARG_KEY_GROUP_ID)
        );
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_items, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        initRecyclerView();
        observeViewModel();
        initToolbar();
        initFab();
    }

    private void observeViewModel() {
        observeItemList();
        observeDisplayLoading();
        observeError();
        observeEventNotifyUserOfDeletion();
        observeEventAdd();
        observeEventEdit();
        observeEventFinish();
    }

    private void observeItemList() {
        viewModel.getItemList().observe(this, items -> adapter.swapData(items));
    }

    private void observeError() {
        viewModel.getEventDisplayError().observe(this, display -> {
            if (display) {
                showError(viewModel.getErrorMessage().getValue());
            } else {
                hideError();
            }
        });
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

    private void observeEventNotifyUserOfDeletion() {
        viewModel.getEventNotifyUserOfDeletion().observe(this, this::notifyDeletionSnackbar);
    }

    private void observeEventAdd() {
        viewModel.getEventAdd().observe(this, this::openAddDialog);
    }

    private void observeEventEdit() {
        viewModel.getEventEdit().observe(this, this::openEditDialog);
    }

    private void observeEventFinish() {
        viewModel.getEventFinish().observe(this, aVoid ->
                getActivity().getSupportFragmentManager().popBackStack()
        );
    }


    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        initLayoutManager(recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        this.adapter = new ItemsAdapter(viewModel, itemTouchHelper);
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

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getArguments().getString(ARG_KEY_GROUP_TITLE));
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
        viewModel.movedPermanently(adapter, newPosition);
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

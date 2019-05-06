package com.example.david.lists.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.databinding.FragmentItemsBinding;
import com.example.david.lists.di.view.DaggerItemsFragmentComponent;
import com.example.david.lists.ui.adapaters.ItemsAdapter;
import com.example.david.lists.ui.adapaters.TouchHelperCallback;
import com.example.david.lists.ui.viewmodels.IItemViewModelContract;
import com.example.david.lists.ui.viewmodels.ItemViewModel;
import com.example.david.lists.ui.viewmodels.ItemViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class ItemsFragment extends Fragment
        implements AddDialogFragment.AddDialogFragmentListener,
        EditDialogFragment.EditDialogFragmentListener,
        TouchHelperCallback.MovementCallback {

    private IItemViewModelContract viewModel;
    @Inject
    ItemViewModelFactory viewsModelFactory;

    private FragmentItemsBinding binding;
    private ItemsAdapter adapter;

    private static final String ARG_KEY_USER_LIST_ID = "user_list_id_key";
    private static final String ARG_KEY_USER_LIST_TITLE = "user_list_title_key";

    public ItemsFragment() {
    }

    static ItemsFragment newInstance(String userListId, String userListTitle) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_USER_LIST_ID, userListId);
        bundle.putString(ARG_KEY_USER_LIST_TITLE, userListTitle);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        inject();
        super.onAttach(context);
    }

    private void inject() {
        DaggerItemsFragmentComponent.builder()
                .application(getActivity().getApplication())
                .userListId(getArguments().getString(ARG_KEY_USER_LIST_ID))
                .build()
                .inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void initViewModel() {
        this.viewModel = ViewModelProviders.of(this, viewsModelFactory).get(ItemViewModel.class);
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
        observeEventDisplayLoading();
        observeEventDisplayError();
        observeEventNotifyUserOfDeletion();
        observeEventAdd();
        observeEventEdit();
        observeEventFinish();
    }

    private void observeItemList() {
        viewModel.getItemList().observe(this, items -> adapter.swapData(items));
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
                .setTitle(getArguments().getString(ARG_KEY_USER_LIST_TITLE));
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

package com.example.david.lists.view.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.R;
import com.example.david.lists.databinding.FragmentBaseBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;
import javax.inject.Provider;

public abstract class FragmentBase extends Fragment
        implements TouchHelperCallback.MovementCallback {

    private FragmentBaseBinding binding;

    @Inject
    Provider<LinearLayoutManager> layoutManger;
    @Inject
    RecyclerView.ItemDecoration dividerItemDecorator;
    @Inject
    ItemTouchHelper itemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base, container, false);
        init();
        return binding.getRoot();
    }


    protected abstract void addButtonClicked();

    protected abstract void undoRecentDeletion();

    protected abstract void deletionNotificationTimedOut();

    protected abstract void draggingListItem(int fromPosition, int toPosition);

    protected abstract void permanentlyMoved(int newPosition);

    protected abstract String getTitle();

    protected abstract boolean enableUpNavigationOnToolbar();

    protected abstract RecyclerView.Adapter getAdapter();


    private void init() {
        initRecyclerView();
        initToolbar();
        initFab();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManger.get());
        recyclerView.addItemDecoration(dividerItemDecorator);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(getAdapter());
    }

    private void initToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getTitle());
        actionBar.setDisplayHomeAsUpEnabled(enableUpNavigationOnToolbar());
    }

    private void initFab() {
        FloatingActionButton fab = binding.fab;
        fabClickListener(fab);
        fabScrollListener(fab);
    }

    private void fabClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view -> addButtonClicked());
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

    protected void notifyDeletionSnackbar(String message) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_undo, view -> undoRecentDeletion())
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        if (validSnackbarEvent(event)) {
                            deletionNotificationTimedOut();
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
        draggingListItem(fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        permanentlyMoved(newPosition);
    }


    protected void openDialogFragment(DialogFragment dialogFragment) {
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }


    protected void showLoading() {
        hideError();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.fab.hide();
    }

    protected void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.fab.show();
    }

    protected void showError(String errorMessage) {
        hideLoading();
        binding.tvError.setText(errorMessage);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    protected void hideError() {
        binding.tvError.setVisibility(View.GONE);
    }
}

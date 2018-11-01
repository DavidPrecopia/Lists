package com.example.david.lists.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.databinding.FragmentListBinding;
import com.example.david.lists.ui.viewmodels.IViewModelContract;
import com.example.david.lists.ui.viewmodels.UtilListViewModels;
import com.example.david.lists.util.MyUtil;
import com.example.david.lists.util.UtilRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ListFragment extends Fragment
        implements AddDialogFragment.AddDialogFragmentListener,
        EditDialogFragment.EditDialogFragmentListener {

    private IViewModelContract viewModel;
    private FragmentListBinding binding;

    private static final String ARG_KEY_DISPLAYING = "displaying_key";
    private static final String ARG_KEY_DISPLAY_MENU = "display_menu_key";

    public ListFragment() {
    }

    static ListFragment newInstance(String displaying, boolean displayOverflowMenu) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_DISPLAYING, displaying);
        bundle.putBoolean(ARG_KEY_DISPLAY_MENU, displayOverflowMenu);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(getArguments().getBoolean(ARG_KEY_DISPLAY_MENU));
        initViewModel();
    }

    private void initViewModel() {
        String currentlyDisplaying = getArguments().getString(ARG_KEY_DISPLAYING);
        assert currentlyDisplaying != null;
        if (currentlyDisplaying.equals(getStringResource(R.string.displaying_user_list))) {
            viewModel = UtilListViewModels.getUserListViewModel(
                    (ListActivity) getActivity(),
                    getActivity().getApplication()
            );
        } else if (currentlyDisplaying.equals(getStringResource(R.string.displaying_item))) {
            viewModel = UtilListViewModels.getItemViewModel(
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
        viewModel.getToolbarTitle().observe(this, title ->
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title)
        );
    }

    private void observeDisplayLoading() {
        viewModel.getEventDisplayLoading().observe(this, display -> {
            if (display) {
                showLoading();
            } else {
                Timber.d("observeDisplayLoading - display: %s", display);
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
        UtilRecyclerView.initLayoutManager(recyclerView);
        viewModel.getItemTouchHelper().attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(viewModel.getAdapter());
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(getMenuResource(), menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private int getMenuResource() {
        return MyUtil.userIsAnonymous() ?
                R.menu.menu_sign_in :
                R.menu.menu_sign_out;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_id_sign_in:
                viewModel.signIn();
                break;
            case R.id.menu_id_sign_out:
                viewModel.signOut();
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
    public void edit(String id, String newTitle) {
        viewModel.changeTitle(id, newTitle);
    }


    private void notifyDeletionSnackbar(String message) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_undo, view -> viewModel.undoRecentDeletion())
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
        Timber.d("hideLoading");
        hideError();
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


    private String getStringResource(int resId) {
        return getActivity().getApplication().getString(resId);
    }
}

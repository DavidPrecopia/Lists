package com.example.david.lists.view.itemlist;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.view.addedit.item.AddEditItemDialog;
import com.example.david.lists.view.common.ListViewBase;
import com.example.david.lists.view.itemlist.buldlogic.DaggerItemListViewComponent;

import javax.inject.Inject;

public class ItemListView extends ListViewBase {

    @Inject
    IItemViewModel viewModel;

    @Inject
    IItemAdapter adapter;

    private static final String ARG_KEY_USER_LIST_ID = "user_list_id_key";
    private static final String ARG_KEY_USER_LIST_TITLE = "user_list_title_key";

    public ItemListView() {
    }

    public static ItemListView newInstance(String userListId, String userListTitle) {
        ItemListView fragment = new ItemListView();
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
        observeViewModel();
    }

    private void inject() {
        DaggerItemListViewComponent.builder()
                .application(getActivity().getApplication())
                .fragment(this)
                .movementCallback(this)
                .userListId(getArguments().getString(ARG_KEY_USER_LIST_ID))
                .build()
                .inject(this);
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
        viewModel.getItemList().observe(this, items -> adapter.submitList(items));
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


    private void openAddDialog(String userListId) {
        openDialogFragment(
                AddEditItemDialog.getInstance("", "", userListId)
        );
    }

    private void openEditDialog(Item item) {
        openDialogFragment(
                AddEditItemDialog.getInstance(item.getId(), item.getTitle(), item.getUserListId())
        );
    }


    @Override
    protected void addButtonClicked() {
        viewModel.addButtonClicked();
    }

    @Override
    protected void undoRecentDeletion() {
        viewModel.undoRecentDeletion(adapter);
    }

    @Override
    protected void deletionNotificationTimedOut() {
        viewModel.deletionNotificationTimedOut();
    }

    @Override
    protected void draggingListItem(int fromPosition, int toPosition) {
        viewModel.dragging(adapter, fromPosition, toPosition);
    }

    @Override
    protected void permanentlyMoved(int newPosition) {
        viewModel.movedPermanently(newPosition);
    }

    @Override
    protected String getTitle() {
        return getArguments().getString(ARG_KEY_USER_LIST_TITLE);
    }

    @Override
    protected boolean enableUpNavigationOnToolbar() {
        return true;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return (RecyclerView.Adapter) adapter;
    }
}

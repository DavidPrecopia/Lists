package com.example.david.lists.view.itemlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.view.addedit.item.AddEditItemDialog;
import com.example.david.lists.view.common.ListViewBase;
import com.example.david.lists.view.itemlist.buldlogic.DaggerItemListViewComponent;

import java.util.List;

import javax.inject.Inject;

public class ItemListView extends ListViewBase
        implements IItemViewContract.View {

    @Inject
    IItemViewContract.Logic logic;

    @Inject
    IItemViewContract.Adapter adapter;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        logic.onStart();
        return view;
    }

    private void inject() {
        DaggerItemListViewComponent.builder()
                .application(getActivity().getApplication())
                .view(this)
                .movementCallback(this)
                .userListId(getArguments().getString(ARG_KEY_USER_LIST_ID))
                .build()
                .inject(this);
    }


    @Override
    public void openAddDialog(String userListId, int position) {
        openDialogFragment(AddEditItemDialog.getInstance(
                "",
                "",
                userListId,
                position
        ));
    }

    @Override
    public void openEditDialog(Item item) {
        openDialogFragment(AddEditItemDialog.getInstance(
                item.getId(),
                item.getTitle(),
                item.getUserListId(),
                item.getPosition()
        ));
    }


    @Override
    public void submitList(List<Item> viewData) {
        adapter.submitList(viewData);
    }


    @Override
    public void notifyUserOfDeletion(String message) {
        super.notifyDeletionSnackbar(message);
    }

    @Override
    public void setStateDisplayList() {
        super.displayList();
    }

    @Override
    public void setStateLoading() {
        super.displayLoading();
    }

    @Override
    public void setStateError(String message) {
        super.displayError(message);
    }

    @Override
    public void showMessage(String message) {
        super.toastMessage(message);
    }

    @Override
    public void finishView() {
        getActivity().finish();
    }


    @Override
    public void onDestroy() {
        logic.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void addButtonClicked() {
        logic.addButtonClicked();
    }

    @Override
    protected void undoRecentDeletion() {
        logic.undoRecentDeletion(adapter);
    }

    @Override
    protected void deletionNotificationTimedOut() {
        logic.deletionNotificationTimedOut();
    }

    @Override
    protected void draggingListItem(int fromPosition, int toPosition) {
        logic.dragging(fromPosition, toPosition, adapter);
    }

    @Override
    protected void permanentlyMoved(int newPosition) {
        logic.movedPermanently(newPosition);
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

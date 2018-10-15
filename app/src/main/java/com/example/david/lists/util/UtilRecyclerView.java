package com.example.david.lists.util;

import android.view.MotionEvent;
import android.view.View;

import com.example.david.lists.R;
import com.example.david.lists.ui.view.ItemTouchHelperCallback;
import com.example.david.lists.ui.viewmodels.IListViewModelContract;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class UtilRecyclerView {
    private UtilRecyclerView() {
    }

    public static void initLayoutManager(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerDecorator(recyclerView, layoutManager));
    }

    private static DividerItemDecoration getDividerDecorator(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
    }


    public static PopupMenu getPopupMenu(int position, View anchor, IListViewModelContract viewModel) {
        PopupMenu popupMenu = new PopupMenu(anchor.getContext(), anchor);
        popupMenu.inflate(R.menu.popup_menu_list_item);
        popupMenu.setOnMenuItemClickListener(getMenuClickListener(position, viewModel));
        return popupMenu;
    }

    private static PopupMenu.OnMenuItemClickListener getMenuClickListener(int position, IListViewModelContract viewModel) {
        return item -> {
            switch (item.getItemId()) {
                case R.id.menu_item_edit:
                    viewModel.edit(position);
                    break;
                case R.id.menu_item_delete:
                    viewModel.delete(position);
                    break;
                default:
                    return false;
            }
            return true;
        };
    }


    public static View.OnTouchListener getDragTouchListener(
            RecyclerView.ViewHolder viewHolder,
            ItemTouchHelperCallback.IStartDragListener startDragListener) {
        return (view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startDragListener.requestDrag(viewHolder);
            }
            view.performClick();
            return true;
        };
    }
}

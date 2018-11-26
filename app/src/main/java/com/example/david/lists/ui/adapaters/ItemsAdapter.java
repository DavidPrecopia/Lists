package com.example.david.lists.ui.adapaters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.ui.view.TouchHelperCallback;
import com.example.david.lists.util.UtilRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public final class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {

    private final List<Item> itemsList;

    private final TouchHelperCallback.IStartDragListener startDragListener;
    private final UtilRecyclerView.PopUpMenuCallback popUpMenuCallback;

    public ItemsAdapter(TouchHelperCallback.IStartDragListener startDragListener,
                        UtilRecyclerView.PopUpMenuCallback popUpMenuCallback) {
        this.startDragListener = startDragListener;
        this.popUpMenuCallback = popUpMenuCallback;
        this.itemsList = new ArrayList<>();
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


    public void swapData(List<Item> newItemsList) {
        itemsList.clear();
        itemsList.addAll(newItemsList);
        notifyDataSetChanged();
    }

    public void move(int fromPosition, int toPosition) {
        Collections.swap(itemsList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void remove(int position) {
        itemsList.remove(position);
        notifyItemRemoved(position);
    }

    public void reAdd(int position, Item item) {
        itemsList.add(position, item);
        notifyItemInserted(position);
    }


    final class ItemsViewHolder extends RecyclerView.ViewHolder {

        private final ListItemBinding binding;

        ItemsViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        private void bindView(Item item) {
            bindTitle(item);
            initDragHandle();
            initPopupMenu();
            binding.executePendingBindings();
        }

        private void bindTitle(Item item) {
            binding.tvTitle.setText(item.getTitle());
        }

        @SuppressLint("ClickableViewAccessibility")
        private void initDragHandle() {
            binding.ivDrag.setOnTouchListener(
                    getDragTouchListener(this, startDragListener)
            );
        }

        private void initPopupMenu() {
            PopupMenu popupMenu = getPopupMenu(
                    getAdapterPosition(), binding.ivOverflowMenu, popUpMenuCallback
            );
            binding.ivOverflowMenu.setOnClickListener(view -> popupMenu.show());
        }

        private PopupMenu getPopupMenu(int position, View anchor, UtilRecyclerView.PopUpMenuCallback viewModel) {
            PopupMenu popupMenu = new PopupMenu(anchor.getContext(), anchor);
            popupMenu.inflate(R.menu.popup_menu_list_item);
            popupMenu.setOnMenuItemClickListener(getMenuClickListener(position, viewModel));
            return popupMenu;
        }

        private PopupMenu.OnMenuItemClickListener getMenuClickListener(int position,
                                                                       UtilRecyclerView.PopUpMenuCallback viewModel) {
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

        private View.OnTouchListener getDragTouchListener(RecyclerView.ViewHolder viewHolder,
                                                          TouchHelperCallback.IStartDragListener startDragListener) {
            return (view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startDragListener.requestDrag(viewHolder);
                }
                view.performClick();
                return true;
            };
        }
    }
}

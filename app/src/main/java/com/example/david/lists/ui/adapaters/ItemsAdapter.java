package com.example.david.lists.ui.adapaters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.ui.viewmodels.IItemViewModelContract;
import com.example.david.lists.util.UtilExceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public final class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>
        implements IItemAdapterContract {

    private final List<Item> itemsList;

    private final IItemViewModelContract viewModel;
    private final ItemTouchHelper itemTouchHelper;
    private final ViewBinderHelper viewBinderHelper;

    public ItemsAdapter(IItemViewModelContract viewModel, ItemTouchHelper itemTouchHelper) {
        this.viewModel = viewModel;
        this.itemTouchHelper = itemTouchHelper;

        viewBinderHelper = new ViewBinderHelper();
        viewBinderHelper.setOpenOnlyOne(true);

        itemsList = new ArrayList<>();
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
        itemsViewHolder.bindView(itemsList.get(itemsViewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public void swapData(List<Item> newItemsList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemDiffUtilCallback(this.itemsList, newItemsList));
        itemsList.clear();
        itemsList.addAll(newItemsList);
        diffResult.dispatchUpdatesTo(this);
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
            bindTitle(item.getTitle());
            initBackgroundView(item.getId());
            initDragHandle();
            initPopupMenu();
            binding.executePendingBindings();
        }

        private void bindTitle(String title) {
            binding.tvTitle.setText(title);
        }

        private void initBackgroundView(String itemId) {
            // Ensures only one row can be opened at a time - see Adapter's constructor.
            viewBinderHelper.bind(binding.swipeRevealLayout, itemId);
            binding.backgroundView.setOnClickListener(view -> {
                viewModel.swipedLeft(ItemsAdapter.this, getAdapterPosition());
                viewBinderHelper.closeLayout(itemId);
            });
        }

        @SuppressLint("ClickableViewAccessibility")
        private void initDragHandle() {
            binding.ivDrag.setOnTouchListener((view, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            itemTouchHelper.startDrag(this);
                        }
                        view.performClick();
                        return true;
                    }
            );
        }

        private void initPopupMenu() {
            binding.ivOverflowMenu.setOnClickListener(view -> getPopupMenu().show());
        }

        private PopupMenu getPopupMenu() {
            PopupMenu popupMenu = new PopupMenu(binding.ivOverflowMenu.getContext(), binding.ivOverflowMenu);
            popupMenu.inflate(R.menu.popup_menu_list_item);
            popupMenu.setOnMenuItemClickListener(getMenuClickListener());
            return popupMenu;
        }

        private PopupMenu.OnMenuItemClickListener getMenuClickListener() {
            return item -> {
                switch (item.getItemId()) {
                    case R.id.menu_item_edit:
                        viewModel.edit(getAdapterPosition());
                        break;
                    case R.id.menu_item_delete:
                        viewModel.delete(ItemsAdapter.this, getAdapterPosition());
                        break;
                    default:
                        UtilExceptions.throwException(new IllegalArgumentException());
                }
                return true;
            };
        }
    }


    final class ItemDiffUtilCallback extends DiffUtil.Callback {
        private final List<Item> oldList;
        private final List<Item> newList;

        ItemDiffUtilCallback(List<Item> oldList, List<Item> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.toString().equals(newList.toString());
        }
    }
}

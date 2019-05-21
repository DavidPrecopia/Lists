package com.example.david.lists.ui.itemlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.ui.ViewHolderBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemsAdapterImpl extends ListAdapter<Item, ItemsAdapterImpl.ItemsViewHolder>
        implements IItemAdapter {

    private final List<Item> itemsList;

    private final IItemViewModel viewModel;
    private final ViewBinderHelper viewBinderHelper;
    private final ItemTouchHelper itemTouchHelper;

    public ItemsAdapterImpl(IItemViewModel viewModel, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        super(DIFF_UTIL_CALLBACK);
        itemsList = new ArrayList<>();
        this.viewModel = viewModel;
        this.viewBinderHelper = viewBinderHelper;
        this.itemTouchHelper = itemTouchHelper;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemsViewHolder(
                ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false),
                viewBinderHelper,
                itemTouchHelper
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder itemsViewHolder, int position) {
        // I am using `getItem()`, not the List field, because when the entire list is updated,
        // the RecyclerView's internal list is updated before the field is.
        Item item = getItem(position);
        itemsViewHolder.bindView(item.getId(), item.getTitle());
    }

    @Override
    public void submitList(@Nullable List<Item> list) {
        super.submitList(list);
        // In case the exact same List is submitted twice
        if (this.itemsList != list) {
            itemsList.clear();
            itemsList.addAll(list);
        }
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


    final class ItemsViewHolder extends ViewHolderBase {
        ItemsViewHolder(ListItemBinding binding, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
            super(binding, viewBinderHelper, itemTouchHelper);
        }

        @Override
        public void swipedLeft(int adapterPosition) {
            viewModel.swipedLeft(ItemsAdapterImpl.this, adapterPosition);
        }

        @Override
        public void edit(int adapterPosition) {
            viewModel.edit(getItem(adapterPosition));
        }

        @Override
        public void delete(int adapterPosition) {
            viewModel.delete(ItemsAdapterImpl.this, adapterPosition);
        }
    }


    private static final DiffUtil.ItemCallback<Item> DIFF_UTIL_CALLBACK = new DiffUtil.ItemCallback<Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    };
}
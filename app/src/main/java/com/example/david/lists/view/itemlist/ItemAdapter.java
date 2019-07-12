package com.example.david.lists.view.itemlist;

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
import com.example.david.lists.view.common.ListItemViewHolderBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemAdapter extends ListAdapter<Item, ItemAdapter.ItemViewHolder>
        implements IItemViewContract.Adapter {

    private final List<Item> itemList;

    private final IItemViewContract.ViewModel viewModel;
    private final ViewBinderHelper viewBinderHelper;
    private final ItemTouchHelper itemTouchHelper;

    public ItemAdapter(IItemViewContract.ViewModel viewModel, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        super(DIFF_UTIL_CALLBACK);
        itemList = new ArrayList<>();
        this.viewModel = viewModel;
        this.viewBinderHelper = viewBinderHelper;
        this.itemTouchHelper = itemTouchHelper;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false),
                viewBinderHelper,
                itemTouchHelper
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemsViewHolder, int position) {
        // I am using `getItem()`, not the List field, because when the entire list is updated,
        // the RecyclerView's internal list is updated before the field is.
        Item item = getItem(position);
        itemsViewHolder.bindView(item.getId(), item.getTitle());
    }

    @Override
    public void submitList(@Nullable List<Item> list) {
        super.submitList(list);
        // In case the exact same List is submitted twice
        if (this.itemList != list) {
            itemList.clear();
            itemList.addAll(list);
        }
    }

    public void move(int fromPosition, int toPosition) {
        Collections.swap(itemList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void remove(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
    }

    public void reAdd(int position, Item item) {
        itemList.add(position, item);
        notifyItemInserted(position);
    }


    final class ItemViewHolder extends ListItemViewHolderBase {
        ItemViewHolder(ListItemBinding binding, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
            super(binding, viewBinderHelper, itemTouchHelper);
        }

        @Override
        protected void swipedLeft(int adapterPosition) {
            viewModel.swipedLeft(ItemAdapter.this, adapterPosition);
        }

        @Override
        protected void edit(int adapterPosition) {
            viewModel.edit(getItem(adapterPosition));
        }

        @Override
        protected void delete(int adapterPosition) {
            viewModel.delete(ItemAdapter.this, adapterPosition);
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

package com.example.david.lists.view.itemlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.view.common.ListItemViewHolderBase;

import java.util.List;

public final class ItemAdapter extends ListAdapter<Item, ItemAdapter.ItemViewHolder>
        implements IItemViewContract.Adapter {

    private final IItemViewContract.Logic logic;
    private final ViewBinderHelper viewBinderHelper;
    private final ItemTouchHelper itemTouchHelper;

    public ItemAdapter(IItemViewContract.Logic logic,
                       ViewBinderHelper viewBinderHelper,
                       ItemTouchHelper itemTouchHelper) {
        super(new ItemDiffCallback());
        this.logic = logic;
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
    }

    public void move(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    public void remove(int position) {
        notifyItemRemoved(position);
    }

    public void reAdd(int position, Item item) {
        notifyItemInserted(position);
    }


    final class ItemViewHolder extends ListItemViewHolderBase {
        ItemViewHolder(ListItemBinding binding, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
            super(binding, viewBinderHelper, itemTouchHelper);
        }

        @Override
        protected void swipedLeft(int adapterPosition) {
            logic.delete(adapterPosition, ItemAdapter.this);
        }

        @Override
        protected void edit(int adapterPosition) {
            logic.edit(adapterPosition);
        }

        @Override
        protected void delete(int adapterPosition) {
            logic.delete(adapterPosition, ItemAdapter.this);
        }
    }
}

package com.example.david.lists.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.david.lists.databinding.ListItemBinding;
import com.example.david.lists.datamodel.Item;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {

    private final List<Item> itemsList;

    ItemsAdapter() {
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


    void swapData(List<Item> newItemsList) {
        itemsList.clear();
        itemsList.addAll(newItemsList);
        notifyDataSetChanged();
    }

    void remove(int position) {
        itemsList.remove(position);
        notifyItemRemoved(position);
    }

    void reAdd(int position, Item item) {
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
            binding.tvTitle.setText(item.getTitle());
            binding.executePendingBindings();
        }
    }
}

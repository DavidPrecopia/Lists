package com.example.david.lists.view.itemlist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.david.lists.data.datamodel.Item;

class ItemDiffCallback extends DiffUtil.ItemCallback<Item> {
    @Override
    public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
        return oldItem.toString().equals(newItem.toString());
    }
}

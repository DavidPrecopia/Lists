package com.example.david.lists.ui.adapaters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.david.lists.data.datamodel.UserList;

/**
 * This is a top-level class because it is also used by the widget's Config Activity.
 */
public final class UserListDiffCallback extends DiffUtil.ItemCallback<UserList> {
    @Override
    public boolean areItemsTheSame(@NonNull UserList oldItem, @NonNull UserList newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull UserList oldItem, @NonNull UserList newItem) {
        return oldItem.toString().equals(newItem.toString());
    }
}

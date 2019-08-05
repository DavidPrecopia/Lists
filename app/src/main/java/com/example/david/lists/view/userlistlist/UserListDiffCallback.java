package com.example.david.lists.view.userlistlist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.david.lists.data.datamodel.UserList;

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

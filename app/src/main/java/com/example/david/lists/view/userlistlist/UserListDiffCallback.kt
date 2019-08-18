package com.example.david.lists.view.userlistlist

import androidx.recyclerview.widget.DiffUtil

import com.example.david.lists.data.datamodel.UserList

class UserListDiffCallback : DiffUtil.ItemCallback<UserList>() {
    override fun areItemsTheSame(oldItem: UserList, newItem: UserList) =
            oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UserList, newItem: UserList) =
            oldItem == newItem
}

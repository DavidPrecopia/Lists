package com.example.david.lists.view.itemlist

import androidx.recyclerview.widget.DiffUtil

import com.example.domain.datamodel.Item

class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item) =
            oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Item, newItem: Item) =
            oldItem == newItem
}

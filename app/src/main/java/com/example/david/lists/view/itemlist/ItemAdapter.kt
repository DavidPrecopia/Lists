package com.example.david.lists.view.itemlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter

import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.david.lists.R
import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.view.common.ListItemViewHolderBase

class ItemAdapter(private val logic: IItemViewContract.Logic,
                  private val viewBinderHelper: ViewBinderHelper,
                  private val itemTouchHelper: ItemTouchHelper) :
        ListAdapter<Item, ItemAdapter.ItemViewHolder>(ItemDiffCallback()),
        IItemViewContract.Adapter {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false),
            viewBinderHelper,
            itemTouchHelper
    )

    override fun onBindViewHolder(itemsViewHolder: ItemViewHolder, position: Int) {
        val (title, _, _, id) = getItem(position)
        itemsViewHolder.bindView(id, title)
    }

    override fun setData(list: List<Item>) {
        super.submitList(list)
    }

    override fun move(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun remove(position: Int) {
        notifyItemRemoved(position)
    }

    override fun reAdd(position: Int, item: Item) {
        notifyItemInserted(position)
    }


    inner class ItemViewHolder(view: View,
                               viewBinderHelper: ViewBinderHelper,
                               itemTouchHelper: ItemTouchHelper) :
            ListItemViewHolderBase(view, viewBinderHelper, itemTouchHelper) {
        override fun swipedLeft(adapterPosition: Int) {
            logic.delete(adapterPosition, this@ItemAdapter)
        }

        override fun edit(adapterPosition: Int) {
            logic.edit(adapterPosition)
        }

        override fun delete(adapterPosition: Int) {
            logic.delete(adapterPosition, this@ItemAdapter)
        }
    }
}

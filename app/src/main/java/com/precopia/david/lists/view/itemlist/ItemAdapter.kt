package com.precopia.david.lists.view.itemlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter

import com.chauthai.swipereveallayout.ViewBinderHelper
import com.precopia.david.lists.R
import com.precopia.david.lists.view.common.ListItemViewHolderBase
import com.precopia.david.lists.view.itemlist.IItemViewContract.LogicEvents
import com.precopia.domain.datamodel.Item

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
            logic.onEvent(LogicEvents.Delete(adapterPosition, this@ItemAdapter))
        }

        override fun edit(adapterPosition: Int) {
            logic.onEvent(LogicEvents.Edit(adapterPosition))
        }

        override fun delete(adapterPosition: Int) {
            logic.onEvent(LogicEvents.Delete(adapterPosition, this@ItemAdapter))
        }
    }
}

package com.example.david.lists.view.common

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TouchHelperCallback(private val movementCallback: MovementCallback) : ItemTouchHelper.Callback() {

    interface MovementCallback {
        fun dragging(fromPosition: Int, toPosition: Int)

        fun movedPermanently(newPosition: Int)
    }


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) =
            makeMovementFlags(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    0
            )

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true


    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        movementCallback.dragging(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}


    /**
     * This is called post [TouchHelperCallback.onMove] and [TouchHelperCallback.onSwiped].
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        movementCallback.movedPermanently(viewHolder.adapterPosition)
    }
}

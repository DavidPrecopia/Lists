package com.precopia.david.lists.view.common

import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.precopia.david.lists.R
import com.precopia.david.lists.util.UtilExceptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item.*

abstract class ListItemViewHolderBase(private val view: View,
                                      private val viewBinderHelper: ViewBinderHelper,
                                      private val itemTouchHelper: ItemTouchHelper) :
        RecyclerView.ViewHolder(view),
        LayoutContainer {

    override val containerView: View?
        get() = view

    private var id: String? = null
    private var title: String? = null

    fun bindView(id: String, title: String) {
        this.id = id
        this.title = title
        bindTitle()
        initBackgroundView()
        initDragHandle()
        initPopupMenu()
    }


    protected abstract fun swipedLeft(adapterPosition: Int)

    protected abstract fun edit(adapterPosition: Int)

    protected abstract fun delete(adapterPosition: Int)


    private fun bindTitle() {
        tv_title.text = title
    }

    private fun initBackgroundView() {
        // Ensures only one row can be opened at a time.
        viewBinderHelper.bind(swipe_reveal_layout, id)
        background_view.setOnClickListener {
            swipedLeft(adapterPosition)
            viewBinderHelper.closeLayout(id)
        }
    }

    private fun initDragHandle() {
        iv_drag.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                itemTouchHelper.startDrag(this)
            }
            view.performClick()
            true
        }
    }

    private fun initPopupMenu() {
        iv_overflow_menu.setOnClickListener { getPopupMenu().show() }
    }

    private fun getPopupMenu() =
            PopupMenu(iv_overflow_menu.context, iv_overflow_menu).apply {
                inflate(R.menu.popup_menu_list_item)
                setOnMenuItemClickListener(getMenuClickListener())
            }

    private fun getMenuClickListener() = PopupMenu.OnMenuItemClickListener {
        when (it.itemId) {
            R.id.menu_item_edit -> edit(adapterPosition)
            R.id.menu_item_delete -> {
                viewBinderHelper.openLayout(id)
                delete(adapterPosition)
            }
            else -> UtilExceptions.throwException(IllegalArgumentException())
        }
        true
    }
}
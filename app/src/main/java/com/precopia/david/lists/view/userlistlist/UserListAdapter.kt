package com.precopia.david.lists.view.userlistlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter

import com.chauthai.swipereveallayout.ViewBinderHelper
import com.precopia.david.lists.R
import com.precopia.david.lists.view.common.ListItemViewHolderBase
import com.precopia.david.lists.view.userlistlist.IUserListViewContract.LogicEvents
import com.precopia.domain.datamodel.UserList
import kotlinx.android.synthetic.main.list_item.*

class UserListAdapter(private val logic: IUserListViewContract.Logic,
                      private val viewBinderHelper: ViewBinderHelper,
                      private val itemTouchHelper: ItemTouchHelper) :
        ListAdapter<UserList, UserListAdapter.UserListViewHolder>(UserListDiffCallback()),
        IUserListViewContract.Adapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false),
            viewBinderHelper,
            itemTouchHelper
    )

    override fun onBindViewHolder(userListViewHolder: UserListViewHolder, position: Int) {
        val (title, _, id) = getItem(position)
        userListViewHolder.bindView(id, title)
    }

    override fun setData(list: List<UserList>) {
        super.submitList(list)
    }

    override fun move(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun remove(position: Int) {
        notifyItemRemoved(position)
    }

    override fun reAdd(position: Int, userList: UserList) {
        notifyItemInserted(position)
    }


    inner class UserListViewHolder(view: View,
                                   viewBinderHelper: ViewBinderHelper,
                                   itemTouchHelper: ItemTouchHelper) :
            ListItemViewHolderBase(view, viewBinderHelper, itemTouchHelper),
            View.OnClickListener {
        init {
            foreground_view.setOnClickListener(this)
        }

        override fun swipedLeft(adapterPosition: Int) {
            logic.onEvent(LogicEvents.Delete(adapterPosition, this@UserListAdapter))
        }

        override fun edit(adapterPosition: Int) {
            logic.onEvent(LogicEvents.Edit(adapterPosition))
        }

        override fun delete(adapterPosition: Int) {
            logic.onEvent(LogicEvents.Delete(adapterPosition, this@UserListAdapter))
        }

        override fun onClick(v: View) {
            logic.onEvent(LogicEvents.UserListSelected(adapterPosition))
        }
    }
}

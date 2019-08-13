package com.example.david.lists.widget.configview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.databinding.WidgetConfigListItemBinding
import com.example.david.lists.view.userlistlist.UserListDiffCallback

class WidgetConfigAdapter(private val logic: IWidgetConfigContract.Logic) :
        ListAdapter<UserList, WidgetConfigAdapter.WidgetConfigViewHolder>(UserListDiffCallback()),
        IWidgetConfigContract.Adapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WidgetConfigViewHolder(
            WidgetConfigListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: WidgetConfigViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun setData(list: List<UserList>) {
        super.submitList(list)
    }


    inner class WidgetConfigViewHolder(private val binding: WidgetConfigListItemBinding) :
            RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bindView(userList: UserList) {
            binding.tvTitle.text = userList.title
        }

        override fun onClick(v: View) {
            logic.selectedUserList(adapterPosition)
        }
    }
}
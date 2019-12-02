package com.example.david.lists.widget.configview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.david.lists.R
import com.example.david.lists.view.userlistlist.UserListDiffCallback
import com.example.domain.datamodel.UserList
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.widget_config_list_item.*

class WidgetConfigAdapter(private val logic: IWidgetConfigContract.Logic) :
        ListAdapter<UserList, WidgetConfigAdapter.WidgetConfigViewHolder>(UserListDiffCallback()),
        IWidgetConfigContract.Adapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WidgetConfigViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.widget_config_list_item, parent, false)
    )

    override fun onBindViewHolder(holder: WidgetConfigViewHolder, position: Int) {
        holder.bindView(getItem(position).title)
    }

    override fun setData(list: List<UserList>) {
        super.submitList(list)
    }


    inner class WidgetConfigViewHolder(private val view: View) :
            RecyclerView.ViewHolder(view),
            LayoutContainer,
            View.OnClickListener {

        override val containerView: View?
            get() = view

        init {
            view.setOnClickListener(this)
        }

        fun bindView(title: String) {
            tv_title.text = title
        }

        override fun onClick(v: View) {
            logic.selectedUserList(adapterPosition)
        }
    }
}
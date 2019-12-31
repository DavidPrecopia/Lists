package com.precopia.david.lists.view.itemlist

import com.precopia.domain.datamodel.Item

interface IItemViewContract {
    interface View {
        fun openAddDialog(userListId: String, position: Int)

        fun openEditDialog(item: Item)

        fun setViewData(viewData: List<Item>)

        fun notifyUserOfDeletion(message: String)

        fun setStateDisplayList()

        fun setStateLoading()

        fun setStateError(message: String)

        fun showMessage(message: String)

        fun finishView()
    }

    interface Logic {
        fun onStart()

        fun add()

        fun edit(position: Int)

        fun dragging(fromPosition: Int, toPosition: Int, adapter: Adapter)

        fun movedPermanently(newPosition: Int)

        fun delete(position: Int, adapter: Adapter)

        fun undoRecentDeletion(adapter: Adapter)

        fun deletionNotificationTimedOut()

        fun onDestroy()
    }

    interface ViewModel {
        val userListId: String

        var viewData: MutableList<Item>

        val tempList: MutableList<Item>

        var tempPosition: Int

        val msgDeletion: String

        fun getMsgListDeleted(title: String): String

        val errorMsg: String

        val errorMsgEmptyList: String

        val errorMsgInvalidUndo: String
    }

    interface Adapter {
        fun setData(list: List<Item>)

        fun move(fromPosition: Int, toPosition: Int)

        fun remove(position: Int)

        fun reAdd(position: Int, item: Item)
    }
}

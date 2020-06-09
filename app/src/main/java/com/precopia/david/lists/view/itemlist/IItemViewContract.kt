package com.precopia.david.lists.view.itemlist

import androidx.lifecycle.LiveData
import com.precopia.domain.datamodel.Item

interface IItemViewContract {
    interface View

    interface Logic {
        fun onEvent(even: LogicEvents)

        fun observe(): LiveData<ViewEvents>
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


    sealed class ViewEvents {
        data class OpenAddDialog(val userListId: String, val position: Int) : ViewEvents()
        data class OpenEditDialog(val item: Item) : ViewEvents()
        data class SetViewData(val viewData: List<Item>) : ViewEvents()
        data class NotifyUserOfDeletion(val message: String) : ViewEvents()
        object SetStateDisplayList : ViewEvents()
        object SetStateLoading : ViewEvents()
        data class SetStateError(val message: String) : ViewEvents()
        data class ShowMessage(val message: String) : ViewEvents()
        object FinishView : ViewEvents()
    }

    sealed class LogicEvents {
        object OnStart : LogicEvents()
        object Add : LogicEvents()
        data class Edit(val position: Int) : LogicEvents()
        data class Dragging(val fromPosition: Int, val toPosition: Int, val adapter: Adapter) : LogicEvents()
        data class MovedPermanently(val newPosition: Int) : LogicEvents()
        data class Delete(val position: Int, val adapter: Adapter) : LogicEvents()
        data class UndoRecentDeletion(val adapter: Adapter) : LogicEvents()
        object DeletionNotificationTimedOut : LogicEvents()
    }
}

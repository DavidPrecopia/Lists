package com.precopia.david.lists.view.userlistlist

import androidx.lifecycle.LiveData
import com.precopia.domain.datamodel.UserList

interface IUserListViewContract {
    interface View

    interface Logic {
        val isNightModeEnabled: Boolean

        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
    }

    interface ViewModel {
        var viewData: MutableList<UserList>

        val tempList: MutableList<UserList>

        var tempPosition: Int

        val msgDeletion: String

        val errorMsg: String

        val errorMsgEmptyList: String

        val errorMsgInvalidUndo: String
    }

    interface Adapter {
        fun setData(list: List<UserList>)

        fun move(fromPosition: Int, toPosition: Int)

        fun remove(position: Int)

        fun reAdd(position: Int, userList: UserList)
    }


    sealed class ViewEvents {
        data class OpenUserList(val userList: UserList) : ViewEvents()
        object OpenPreferences : ViewEvents()
        data class OpenAddDialog(val position: Int) : ViewEvents()
        data class OpenEditDialog(val userList: UserList) : ViewEvents()
        data class SetViewData(val viewData: List<UserList>) : ViewEvents()
        data class NotifyUserOfDeletion(val message: String) : ViewEvents()
        object SetStateDisplayList : ViewEvents()
        object SetStateLoading : ViewEvents()
        data class SetStateError(val message: String) : ViewEvents()
        data class ShowMessage(val message: String) : ViewEvents()
    }

    sealed class LogicEvents {
        object OnStart : LogicEvents()
        data class UserListSelected(val position: Int) : LogicEvents()
        object Add : LogicEvents()
        data class Edit(val position: Int) : LogicEvents()
        data class Dragging(val fromPosition: Int, val toPosition: Int, val adapter: Adapter) : LogicEvents()
        data class MovedPermanently(val newPosition: Int) : LogicEvents()
        data class Delete(val position: Int, val adapter: Adapter) : LogicEvents()
        data class UndoRecentDeletion(val adapter: Adapter) : LogicEvents()
        object DeletionNotificationTimedOut : LogicEvents()
        object PreferencesSelected : LogicEvents()
        data class SetNightMode(val isMenuItemChecked: Boolean) : LogicEvents()
    }
}

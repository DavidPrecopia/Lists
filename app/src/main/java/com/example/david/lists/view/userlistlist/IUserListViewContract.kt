package com.example.david.lists.view.userlistlist

import com.example.david.lists.data.datamodel.UserList

interface IUserListViewContract {
    interface View {
        fun openUserList(userList: UserList)

        fun confirmSignOut()

        fun signOut(resultCode: Int)

        fun openAddDialog(position: Int)

        fun openEditDialog(userList: UserList)

        fun setViewData(viewData: List<UserList>)

        fun notifyUserOfDeletion(message: String)

        fun setStateDisplayList()

        fun setStateLoading()

        fun setStateError(message: String)
    }

    interface Logic {
        val isNightModeEnabled: Boolean

        fun onStart()

        fun userListSelected(position: Int)

        fun add()

        fun edit(position: Int)

        fun dragging(fromPosition: Int, toPosition: Int, adapter: Adapter)

        fun movedPermanently(newPosition: Int)

        fun delete(position: Int, adapter: Adapter)

        fun undoRecentDeletion(adapter: Adapter)

        fun deletionNotificationTimedOut()

        fun signOut()

        fun signOutConfirmed()

        fun setNightMode(isMenuItemChecked: Boolean)

        fun onDestroy()
    }

    interface ViewModel {
        var viewData: MutableList<UserList>

        val tempList: MutableList<UserList>

        var tempPosition: Int

        val msgInvalidUndo: String

        val msgDeletion: String

        val errorMsg: String

        val errorMsgEmptyList: String

        val signOutResultCode: Int
    }

    interface Adapter {
        fun setData(list: List<UserList>)

        fun move(fromPosition: Int, toPosition: Int)

        fun remove(position: Int)

        fun reAdd(position: Int, userList: UserList)
    }
}

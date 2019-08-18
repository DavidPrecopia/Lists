package com.example.david.lists.view.userlistlist

import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.view.authentication.IAuthContract

interface IUserListViewContract {
    interface View {
        fun openUserList(userList: UserList)

        fun confirmSignOut()

        fun openAuthentication(authGoal: IAuthContract.AuthGoal, requestCode: Int, intentExtraAuthResultKey: String)

        fun openAddDialog(position: Int)

        fun openEditDialog(userList: UserList)

        fun setViewData(viewData: List<UserList>)

        fun notifyUserOfDeletion(message: String)

        fun setStateDisplayList()

        fun setStateLoading()

        fun setStateError(message: String)

        fun recreateView()
    }

    interface Logic {
        val isUserAnon: Boolean

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

        fun signIn()

        fun authResult(authResult: IAuthContract.AuthResult)

        fun setNightMode(isMenuItemChecked: Boolean)

        fun onDestroy()
    }

    interface ViewModel {
        var viewData: MutableList<UserList>

        val tempList: MutableList<UserList>

        var tempPosition: Int

        val requestCode: Int

        val intentExtraAuthResultKey: String

        val msgInvalidUndo: String

        val msgDeletion: String

        val errorMsg: String

        val errorMsgEmptyList: String
    }

    interface Adapter {
        fun setData(list: List<UserList>)

        fun move(fromPosition: Int, toPosition: Int)

        fun remove(position: Int)

        fun reAdd(position: Int, userList: UserList)
    }
}

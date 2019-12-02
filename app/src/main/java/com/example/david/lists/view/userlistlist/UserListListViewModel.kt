package com.example.david.lists.view.userlistlist

import android.app.Application
import com.example.david.lists.R
import com.example.david.lists.view.common.ViewModelBase
import com.example.domain.datamodel.UserList
import java.util.*

class UserListListViewModel(application: Application) : ViewModelBase(application),
        IUserListViewContract.ViewModel {

    override var viewData: MutableList<UserList> = ArrayList()

    override val tempList: MutableList<UserList> = ArrayList()

    override var tempPosition: Int = -1


    override val msgDeletion: String
        get() = getStringRes(R.string.msg_user_list_deletion)


    override val errorMsg: String
        get() = getStringRes(R.string.error_msg_generic)

    override val errorMsgEmptyList: String
        get() = getStringRes(R.string.error_msg_no_user_lists)

    override val errorMsgInvalidUndo: String
        get() = getStringRes(R.string.error_msg_invalid_action_undo_deletion)
}

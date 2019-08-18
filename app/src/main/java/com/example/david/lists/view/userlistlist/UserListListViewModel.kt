package com.example.david.lists.view.userlistlist

import android.app.Application
import com.example.david.lists.R
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.view.common.ViewModelBase
import java.util.*

class UserListListViewModel(application: Application, override val requestCode: Int) :
        ViewModelBase(application),
        IUserListViewContract.ViewModel {

    override var viewData: MutableList<UserList> = ArrayList()

    override val tempList: MutableList<UserList> = ArrayList()

    override var tempPosition: Int = -1


    override val intentExtraAuthResultKey: String
        get() = getStringRes(R.string.intent_extra_auth_result)


    override val msgInvalidUndo: String
        get() = getStringRes(R.string.error_msg_invalid_action_undo_deletion)

    override val msgDeletion: String
        get() = getStringRes(R.string.msg_user_list_deletion)

    override val errorMsg: String
        get() = getStringRes(R.string.error_msg_generic)

    override val errorMsgEmptyList: String
        get() = getStringRes(R.string.error_msg_no_user_lists)
}

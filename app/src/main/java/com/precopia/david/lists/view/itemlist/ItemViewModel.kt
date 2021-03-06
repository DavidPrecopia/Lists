package com.precopia.david.lists.view.itemlist

import com.precopia.david.lists.R
import com.precopia.domain.datamodel.Item
import java.util.*

class ItemViewModel(private val getStringRes: (Int) -> String,
                    private val getStringResArg: (Int, String) -> String,
                    override val userListId: String) :
        IItemViewContract.ViewModel {

    override var viewData: MutableList<Item> = ArrayList()

    override val tempList: MutableList<Item> = ArrayList()

    override var tempPosition: Int = -1


    override val msgDeletion: String
        get() = getStringRes(R.string.msg_item_deletion)

    override fun getMsgListDeleted(title: String) =
            getStringResArg(R.string.msg_user_list_deletion_parameter, title)


    override val errorMsg: String
        get() = getStringRes(R.string.error_msg_generic)

    override val errorMsgEmptyList: String
        get() = getStringRes(R.string.error_msg_empty_user_list)

    override val errorMsgInvalidUndo: String
        get() = getStringRes(R.string.error_msg_invalid_action_undo_deletion)
}

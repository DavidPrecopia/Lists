package com.precopia.david.lists.view.addedit.common

import com.precopia.david.lists.R
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType

class AddEditViewModel(private val getStringRes: (Int) -> String) : IAddEditContract.ViewModel {

    override lateinit var taskType: TaskType


    override lateinit var id: String

    override lateinit var currentTitle: String

    override var userListId: String? = null

    override var position: Int = 0


    override val msgError: String
        get() = getStringRes(R.string.error_msg_generic)

    override val msgEmptyTitle: String
        get() = getStringRes(R.string.error_msg_empty_title_text_field)

    override val msgTitleUnchanged: String
        get() = getStringRes(R.string.error_msg_title_unchanged)
}

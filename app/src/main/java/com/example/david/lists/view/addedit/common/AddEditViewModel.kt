package com.example.david.lists.view.addedit.common

import android.app.Application
import com.example.david.lists.R
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType

class AddEditViewModel(private val application: Application) : IAddEditContract.ViewModel {

    override lateinit var taskType: TaskType


    override lateinit var id: String

    override lateinit var currentTitle: String

    override var userListId: String? = null

    override var position: Int = 0


    override val msgEmptyTitle: String
        get() = application.getString(R.string.error_msg_empty_title_text_field)

    override val msgTitleUnchanged: String
        get() = application.getString(R.string.error_msg_title_unchanged)
}

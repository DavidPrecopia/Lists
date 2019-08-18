package com.example.david.lists.view.addedit.common

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT

abstract class AddEditLogicBase(protected val view: IAddEditContract.View,
                                protected val viewModel: IAddEditContract.ViewModel,
                                protected val repo: IRepositoryContract.Repository,
                                id: String,
                                title: String,
                                userListId: String?,
                                position: Int) : IAddEditContract.Logic {

    init {
        viewModel.id = id
        viewModel.currentTitle = title
        viewModel.userListId = userListId
        viewModel.position = position

        viewModel.taskType = if (title.isEmpty()) ADD else EDIT
    }

    protected abstract fun save(newTitle: String)

    override val currentTitle: String
        get() = viewModel.currentTitle

    override fun validateInput(input: String) {
        when {
            input.isBlank() -> view.setStateError(viewModel.msgEmptyTitle)
            titleUnchanged(input) -> view.setStateError(viewModel.msgTitleUnchanged)
            else -> {
                save(input)
                view.finishView()
            }
        }
    }

    private fun titleUnchanged(input: String) = input == viewModel.currentTitle
}

package com.example.david.lists.view.addedit.common

import com.example.david.lists.common.subscribeCompletable
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.example.domain.repository.IRepositoryContract
import io.reactivex.Completable

abstract class AddEditLogicBase(protected val view: IAddEditContract.View,
                                protected val viewModel: IAddEditContract.ViewModel,
                                protected val repo: IRepositoryContract.Repository,
                                private val schedulerProvider: ISchedulerProviderContract,
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

    protected fun saveWithCompletable(completable: Completable) {
        subscribeCompletable(
                completable,
                {},
                { UtilExceptions.throwException(it) },
                schedulerProvider
        )
    }

    private fun titleUnchanged(input: String) = input == viewModel.currentTitle
}

package com.precopia.david.lists.view.addedit.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.addedit.common.IAddEditContract.LogicEvents
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.precopia.david.lists.view.addedit.common.IAddEditContract.ViewEvents
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.core.Completable

abstract class AddEditLogicBase(protected val viewModel: IAddEditContract.ViewModel,
                                protected val repo: IRepositoryContract.Repository,
                                private val schedulerProvider: ISchedulerProviderContract,
                                id: String,
                                title: String,
                                userListId: String?,
                                position: Int) :
        ViewModel(),
        IAddEditContract.Logic {

    init {
        viewModel.id = id
        viewModel.currentTitle = title
        viewModel.userListId = userListId
        viewModel.position = position

        viewModel.taskType = if (title.isEmpty()) ADD else EDIT
    }

    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override val currentTitle: String
        get() = viewModel.currentTitle


    override fun onEvent(event: LogicEvents) {
        when (event) {
            is LogicEvents.Save -> validateInput(event.input)
        }
    }

    override fun observe(): LiveData<ViewEvents> = viewEventLiveData


    protected abstract fun save(newTitle: String)


    private fun validateInput(input: String) {
        when {
            input.isBlank() -> viewEventLiveData.value =
                    ViewEvents.SetStateError(viewModel.msgEmptyTitle)
            titleUnchanged(input) -> viewEventLiveData.value =
                    ViewEvents.SetStateError(viewModel.msgTitleUnchanged)
            else -> {
                save(input)
                viewEventLiveData.value = ViewEvents.FinishView
            }
        }
    }

    protected fun saveWithCompletable(completable: Completable) {
        subscribeCompletable(
                completable,
                { /*intentionally empty*/ },
                {
                    viewEventLiveData.value = ViewEvents.DisplayMessage(viewModel.msgError)
                    UtilExceptions.throwException(it)
                },
                schedulerProvider
        )
    }

    private fun titleUnchanged(input: String) = input == viewModel.currentTitle
}

package com.precopia.david.lists.view.addedit.common

import androidx.lifecycle.LiveData

interface IAddEditContract {
    interface View

    interface Logic {
        val currentTitle: String

        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
    }

    interface ViewModel {
        var taskType: TaskType

        var id: String

        var currentTitle: String

        var userListId: String?

        var position: Int

        val msgError: String

        val msgEmptyTitle: String

        val msgTitleUnchanged: String
    }

    enum class TaskType {
        ADD,
        EDIT
    }


    sealed class ViewEvents {
        data class SetStateError(val message: String): ViewEvents()
        data class DisplayMessage(val message: String): ViewEvents()
        object FinishView: ViewEvents()
    }

    sealed class LogicEvents {
        data class Save(val input: String): LogicEvents()
    }
}

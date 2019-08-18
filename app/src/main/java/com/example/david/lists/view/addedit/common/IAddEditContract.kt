package com.example.david.lists.view.addedit.common

interface IAddEditContract {
    interface View {
        fun setStateError(message: String)

        fun finishView()
    }

    interface Logic {
        val currentTitle: String

        fun validateInput(input: String)
    }

    interface ViewModel {
        var taskType: TaskType

        var id: String

        var currentTitle: String

        var userListId: String?

        var position: Int

        val msgEmptyTitle: String

        val msgTitleUnchanged: String
    }

    enum class TaskType {
        ADD,
        EDIT
    }
}

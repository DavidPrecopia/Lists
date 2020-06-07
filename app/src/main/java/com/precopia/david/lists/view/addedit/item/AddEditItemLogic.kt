package com.precopia.david.lists.view.addedit.item

import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.addedit.common.AddEditLogicBase
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.precopia.domain.repository.IRepositoryContract

class AddEditItemLogic(viewModel: IAddEditContract.ViewModel,
                       repo: IRepositoryContract.Repository,
                       schedulerProvider: ISchedulerProviderContract,
                       id: String,
                       title: String,
                       userListId: String,
                       position: Int) :
        AddEditLogicBase(viewModel, repo, schedulerProvider, id, title, userListId, position) {

    override fun save(newTitle: String) {
        when (viewModel.taskType) {
            ADD -> saveWithCompletable(repo.addItem(newTitle, viewModel.position, viewModel.userListId!!))
            EDIT -> saveWithCompletable(repo.renameItem(viewModel.id, newTitle))
        }
    }
}

package com.precopia.david.lists.view.addedit.userlist

import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.addedit.common.AddEditLogicBase
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.precopia.domain.repository.IRepositoryContract

class AddEditUserListLogic(viewModel: IAddEditContract.ViewModel,
                           repo: IRepositoryContract.Repository,
                           schedulerProvider: ISchedulerProviderContract,
                           id: String,
                           title: String,
                           position: Int) :
        AddEditLogicBase(viewModel, repo, schedulerProvider, id, title, null, position) {

    override fun save(newTitle: String) {
        when (viewModel.taskType) {
            ADD -> saveWithCompletable(repo.addUserList(newTitle, viewModel.position))
            EDIT -> saveWithCompletable(repo.renameUserList(viewModel.id, newTitle))
        }
    }
}

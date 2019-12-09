package com.example.david.lists.view.addedit.userlist

import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.addedit.common.AddEditLogicBase
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.example.domain.repository.IRepositoryContract

class AddEditUserListLogic(view: IAddEditContract.View,
                           viewModel: IAddEditContract.ViewModel,
                           repo: IRepositoryContract.Repository,
                           schedulerProvider: ISchedulerProviderContract,
                           id: String,
                           title: String,
                           position: Int) :
        AddEditLogicBase(view, viewModel, repo, schedulerProvider, id, title, null, position) {

    public override fun save(newTitle: String) {
        when (viewModel.taskType) {
            ADD -> saveWithCompletable(repo.addUserList(newTitle, viewModel.position))
            EDIT -> saveWithCompletable(repo.renameUserList(viewModel.id, newTitle))
        }
    }
}

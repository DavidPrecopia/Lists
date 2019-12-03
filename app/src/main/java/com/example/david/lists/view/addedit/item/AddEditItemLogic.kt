package com.example.david.lists.view.addedit.item

import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.addedit.common.AddEditLogicBase
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.example.domain.datamodel.Item
import com.example.domain.repository.IRepositoryContract

class AddEditItemLogic(view: IAddEditContract.View,
                       viewModel: IAddEditContract.ViewModel,
                       repo: IRepositoryContract.Repository,
                       schedulerProvider: ISchedulerProviderContract,
                       id: String,
                       title: String,
                       userListId: String,
                       position: Int) :
        AddEditLogicBase(view, viewModel, repo, schedulerProvider, id, title, userListId, position) {

    public override fun save(newTitle: String) {
        when (viewModel.taskType) {
            ADD -> saveWithCompletable(repo.addItem(Item(newTitle, viewModel.position, viewModel.userListId!!)))
            EDIT -> saveWithCompletable(repo.renameItem(viewModel.id, newTitle))
        }
    }
}

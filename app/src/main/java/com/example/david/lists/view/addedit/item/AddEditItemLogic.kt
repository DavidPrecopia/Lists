package com.example.david.lists.view.addedit.item

import com.example.androiddata.repository.IRepositoryContract
import com.example.david.lists.view.addedit.common.AddEditLogicBase
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.example.domain.datamodel.Item

class AddEditItemLogic(view: IAddEditContract.View,
                       viewModel: IAddEditContract.ViewModel,
                       repo: IRepositoryContract.Repository,
                       id: String,
                       title: String,
                       userListId: String,
                       position: Int) :
        AddEditLogicBase(view, viewModel, repo, id, title, userListId, position) {

    public override fun save(newTitle: String) {
        when (viewModel.taskType) {
            ADD -> repo.addItem(Item(newTitle, viewModel.position, viewModel.userListId!!))
            EDIT -> repo.renameItem(viewModel.id, newTitle)
        }
    }
}

package com.example.david.lists.view.addedit.userlist

import com.example.david.lists.view.addedit.common.AddEditLogicBase
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.example.domain.datamodel.UserList
import com.example.domain.repository.IRepositoryContract

class AddEditUserListLogic(view: IAddEditContract.View,
                           viewModel: IAddEditContract.ViewModel,
                           repo: IRepositoryContract.Repository,
                           id: String,
                           title: String,
                           position: Int) :
        AddEditLogicBase(view, viewModel, repo, id, title, null, position) {

    public override fun save(newTitle: String) {
        when (viewModel.taskType) {
            ADD -> repo.addUserList(UserList(newTitle, viewModel.position))
            EDIT -> repo.renameUserList(viewModel.id, newTitle)
        }
    }
}

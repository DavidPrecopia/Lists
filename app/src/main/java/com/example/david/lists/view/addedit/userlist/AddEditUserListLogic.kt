package com.example.david.lists.view.addedit.userlist

import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.addedit.common.AddEditLogicBase
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT

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

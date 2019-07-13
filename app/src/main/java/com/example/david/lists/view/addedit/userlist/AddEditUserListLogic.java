package com.example.david.lists.view.addedit.userlist;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.addedit.common.AddEditLogicBase;
import com.example.david.lists.view.addedit.common.IAddEditContract;

import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_ADD;
import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_EDIT;

public class AddEditUserListLogic extends AddEditLogicBase {

    public AddEditUserListLogic(IAddEditContract.View view,
                                IAddEditContract.ViewModel viewModel,
                                IRepositoryContract.Repository repository,
                                String id,
                                String title,
                                int position) {
        super(view, viewModel, repository, id, title, null, position);
    }


    @Override
    public void save(String newTitle) {
        if (viewModel.getCurrentTaskType() == TASK_ADD) {
            repository.addUserList(new UserList(newTitle, viewModel.getLastPosition()));
        } else if (viewModel.getCurrentTaskType() == TASK_EDIT) {
            repository.renameUserList(viewModel.getId(), newTitle);
        }
    }
}

package com.example.david.lists.view.addedit.item;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.addedit.common.AddEditLogicBase;
import com.example.david.lists.view.addedit.common.IAddEditContract;

import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_ADD;
import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_EDIT;

public class AddEditItemLogic extends AddEditLogicBase {

    public AddEditItemLogic(IAddEditContract.View view,
                            IAddEditContract.ViewModel viewModel,
                            IRepositoryContract.Repository repository,
                            String id,
                            String title,
                            String userListId,
                            int position) {
        super(view, viewModel, repository, id, title, userListId, position);
    }


    @Override
    public void save(String newTitle) {
        if (viewModel.getTaskType() == TASK_ADD) {
            repository.addItem(new Item(newTitle, viewModel.getPosition(), viewModel.getUserListId()));
        } else if (viewModel.getTaskType() == TASK_EDIT) {
            repository.renameItem(viewModel.getId(), newTitle);
        }
    }
}

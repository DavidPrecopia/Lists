package com.example.david.lists.view.addedit.common;

import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;

import javax.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;

import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_ADD;
import static com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.TASK_EDIT;

public abstract class AddEditLogicBase implements IAddEditContract.Logic {

    protected final IAddEditContract.View view;
    protected final IAddEditContract.ViewModel viewModel;

    protected final IRepositoryContract.Repository repository;
    protected final ISchedulerProviderContract schedulerProvider;
    protected final CompositeDisposable disposable;

    public AddEditLogicBase(IAddEditContract.View view,
                            IAddEditContract.ViewModel viewModel,
                            IRepositoryContract.Repository repository,
                            ISchedulerProviderContract schedulerProvider,
                            CompositeDisposable disposable,
                            String id,
                            String title,
                            @Nullable String userListId) {
        this.view = view;
        this.viewModel = viewModel;
        this.repository = repository;
        this.schedulerProvider = schedulerProvider;
        this.disposable = disposable;

        viewModel.setId(id);
        viewModel.setCurrentTitle(title);
        viewModel.setUserListId(userListId);

        setTaskType();
    }

    private void setTaskType() {
        viewModel.setTaskType(
                emptyString(viewModel.getCurrentTitle()) ? TASK_ADD : TASK_EDIT
        );
    }


    protected abstract void save(String newTitle);


    @Override
    public String getCurrentTitle() {
        return viewModel.getCurrentTitle();
    }

    @Override
    public void validateInput(String input) {
        if (emptyString(input)) {
            view.setStateError(viewModel.getMsgEmptyTitle());
        }  else if (titleUnchanged(input)) {
            view.setStateError(viewModel.getMsgTitleUnchanged());
        } else {
            save(input);
            view.finishView();
        }
    }

    private boolean emptyString(String input) {
        return input == null || input.isEmpty();
    }

    private boolean titleUnchanged(String input) {
        return input.equals(viewModel.getCurrentTitle());
    }


    @Override
    public void destroy() {
        disposable.clear();
    }
}

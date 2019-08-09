package com.example.david.lists.widget.configview;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.common.ListViewLogicBase;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public class WidgetConfigLogic extends ListViewLogicBase
        implements IWidgetConfigContract.Logic {

    private final IWidgetConfigContract.View view;
    private final IWidgetConfigContract.ViewModel viewModel;

    public WidgetConfigLogic(IWidgetConfigContract.View view,
                             IWidgetConfigContract.ViewModel viewModel,
                             IRepositoryContract.Repository repo,
                             ISchedulerProviderContract schedulerProvider,
                             CompositeDisposable disposable) {
        super(repo, schedulerProvider, disposable);
        this.view = view;
        this.viewModel = viewModel;
    }


    @Override
    public void onStart(int widgetId) {
        verifyWidgetId(widgetId);
        view.setStateLoading();
        getUserLists();
    }

    private void verifyWidgetId(int widgetId) {
        viewModel.setWidgetId(widgetId);
        // In case the user cancels without selecting anything.
        view.setResults(widgetId, viewModel.getResultCancelled());

        if (widgetId == viewModel.getInvalidWidgetId()) {
            view.finishViewInvalidId();
        }
    }


    private void getUserLists() {
        disposable.add(repo.getAllUserLists()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribeWith(userListSubscriber())
        );
    }

    private DisposableSubscriber<List<UserList>> userListSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> userLists) {
                viewModel.setViewData(userLists);
                evaluateNewData();
            }

            @Override
            public void onError(Throwable t) {
                view.setStateError(viewModel.getErrorMsg());
                UtilExceptions.throwException(t);
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void evaluateNewData() {
        List<UserList> userLists = viewModel.getViewData();
        view.setData(userLists);

        if (userLists.isEmpty()) {
            view.setStateError(viewModel.getErrorMsgEmptyList());
        } else {
            view.setStateDisplayList();
        }
    }


    @Override
    public void selectedUserList(UserList userList) {
        saveDetails(userList.getId(), userList.getTitle());
        view.setResults(viewModel.getWidgetId(), viewModel.getResultOk());
        view.finishView(viewModel.getWidgetId());
    }

    private void saveDetails(String id, String title) {
        view.saveDetails(
                id,
                title,
                viewModel.getSharedPrefKeyId(),
                viewModel.getSharedPrefKeyTitle()
        );
    }


    @Override
    public void onDestroy() {
        disposable.clear();
    }
}

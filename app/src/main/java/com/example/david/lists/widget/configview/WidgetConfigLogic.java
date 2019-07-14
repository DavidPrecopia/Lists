package com.example.david.lists.widget.configview;

import android.content.SharedPreferences;

import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.util.UtilExceptions;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

public class WidgetConfigLogic implements IWidgetConfigContract.Logic {

    private final IWidgetConfigContract.View view;
    private final IWidgetConfigContract.ViewModel viewModel;

    private final IRepositoryContract.Repository repo;
    private final ISchedulerProviderContract schedulerProvider;
    private final CompositeDisposable disposable;

    private final SharedPreferences sharedPrefs;

    public WidgetConfigLogic(IWidgetConfigContract.View view,
                             IWidgetConfigContract.ViewModel viewModel,
                             IRepositoryContract.Repository repo,
                             ISchedulerProviderContract schedulerProvider,
                             CompositeDisposable disposable,
                             SharedPreferences sharedPrefs,
                             int widgetId) {
        this.view = view;
        this.viewModel = viewModel;
        this.repo = repo;
        this.schedulerProvider = schedulerProvider;
        this.disposable = disposable;
        this.sharedPrefs = sharedPrefs;

        init(widgetId);
    }

    private void init(int widgetId) {
        viewModel.setWidgetId(widgetId);
        // In case the user cancels without selecting anything.
        view.setResults(widgetId, viewModel.getResultCancelled());

        if (widgetId == viewModel.getInvalidWidgetId()) {
            view.finishViewInvalidId();
        }
    }


    @Override
    public void onStart(int widgetId) {
        view.setStateLoading();
        getUserLists();
    }


    private void getUserLists() {
        disposable.add(repo.getAllUserLists()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
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
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(viewModel.getSharedPrefKeyId(), id);
        editor.putString(viewModel.getSharedPrefKeyTitle(), title);
        editor.apply();
    }


    @Override
    public void onDestroy() {
        disposable.clear();
    }
}

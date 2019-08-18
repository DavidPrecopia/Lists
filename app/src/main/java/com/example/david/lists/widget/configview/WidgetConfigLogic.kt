package com.example.david.lists.widget.configview

import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.common.ListViewLogicBase

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.DisposableSubscriber

class WidgetConfigLogic(private val view: IWidgetConfigContract.View,
                        private val viewModel: IWidgetConfigContract.ViewModel,
                        widgetId: Int,
                        repo: IRepositoryContract.Repository,
                        schedulerProvider: ISchedulerProviderContract,
                        disposable: CompositeDisposable) :
        ListViewLogicBase(repo, schedulerProvider, disposable),
        IWidgetConfigContract.Logic {

    init {
        viewModel.widgetId = widgetId
        // In case the user cancels without selecting anything.
        view.setResults(widgetId, viewModel.resultCancelled)

        if (widgetId == viewModel.invalidWidgetId) {
            view.finishViewInvalidId()
        }
    }


    override fun onStart() {
        view.setStateLoading()
        getUserLists()
    }


    private fun getUserLists() {
        disposable.add(repo.getUserLists
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribeWith(userListSubscriber())
        )
    }

    private fun userListSubscriber() = object : DisposableSubscriber<List<UserList>>() {
        override fun onNext(userLists: List<UserList>) {
            viewModel.viewData = userLists
            evalNewData()
        }

        override fun onError(t: Throwable) {
            view.setStateError(viewModel.errorMsg)
            UtilExceptions.throwException(t)
        }

        override fun onComplete() {

        }
    }

    private fun evalNewData() {
        view.setViewData(viewModel.viewData)

        if (viewModel.viewData.isEmpty()) {
            view.setStateError(viewModel.errorMsgEmptyList)
        } else {
            view.setStateDisplayList()
        }
    }


    override fun selectedUserList(position: Int) {
        val userList = viewModel.viewData[position]
        saveDetails(userList.id, userList.title)
        view.setResults(viewModel.widgetId, viewModel.resultOk)
        view.finishView(viewModel.widgetId)
    }

    private fun saveDetails(id: String, title: String) {
        view.saveDetails(
                id,
                title,
                viewModel.sharedPrefKeyId,
                viewModel.sharedPrefKeyTitle
        )
    }


    override fun onDestroy() {
        disposable.clear()
    }
}

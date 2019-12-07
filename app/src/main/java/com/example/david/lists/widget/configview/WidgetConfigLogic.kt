package com.example.david.lists.widget.configview

import com.example.david.lists.common.subscribeFlowableUserList
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.common.ListViewLogicBase
import com.example.domain.datamodel.UserList
import com.example.domain.repository.IRepositoryContract
import io.reactivex.disposables.CompositeDisposable

class WidgetConfigLogic(private val view: IWidgetConfigContract.View,
                        private val viewModel: IWidgetConfigContract.ViewModel,
                        repo: IRepositoryContract.Repository,
                        schedulerProvider: ISchedulerProviderContract,
                        disposable: CompositeDisposable) :
        ListViewLogicBase(repo, schedulerProvider, disposable),
        IWidgetConfigContract.Logic {


    override fun onStart(widgetId: Int) {
        view.setStateLoading()
        viewModel.widgetId = widgetId
        view.setResults(widgetId, viewModel.resultCancelled)

        when (widgetId) {
            viewModel.invalidWidgetId -> view.finishViewInvalidId()
            else -> getUserLists()
        }
    }


    private fun getUserLists() {
        disposable.add(subscribeFlowableUserList(
                repo.getUserLists(),
                { onNextList(it) },
                { onObservableError(it) },
                schedulerProvider
        ))
    }

    private fun onNextList(list: List<UserList>) {
        viewModel.viewData = list
        evalNewData()
    }

    private fun onObservableError(t: Throwable) {
        view.setStateError(viewModel.errorMsg)
        UtilExceptions.throwException(t)
    }


    private fun evalNewData() {
        view.setViewData(viewModel.viewData)
        when {
            viewModel.viewData.isEmpty() -> view.setStateError(viewModel.errorMsgEmptyList)
            else -> view.setStateDisplayList()
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

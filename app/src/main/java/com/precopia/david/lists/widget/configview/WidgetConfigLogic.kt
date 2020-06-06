package com.precopia.david.lists.widget.configview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.precopia.david.lists.common.subscribeFlowableUserList
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.common.ListViewLogicBase
import com.precopia.david.lists.widget.configview.IWidgetConfigContract.LogicEvents
import com.precopia.david.lists.widget.configview.IWidgetConfigContract.ViewEvents
import com.precopia.domain.datamodel.UserList
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable

class WidgetConfigLogic(private val viewModel: IWidgetConfigContract.ViewModel,
                        repo: IRepositoryContract.Repository,
                        schedulerProvider: ISchedulerProviderContract,
                        disposable: CompositeDisposable) :
        ListViewLogicBase(repo, schedulerProvider, disposable),
        IWidgetConfigContract.Logic {

    private val viewEventLiveData = MutableLiveData<ViewEvents>()

    override fun onEvent(event: LogicEvents) {
        when (event) {
            is LogicEvents.OnStart -> onStart(event.widgetId)
            is LogicEvents.SelectedUserList -> selectedUserList(event.position)
        }
    }

    private fun onStart(widgetId: Int) {
        viewEventLiveData.value = ViewEvents.SetStateLoading
        viewModel.widgetId = widgetId
        viewEventLiveData.value = ViewEvents.SetResults(
                widgetId, viewModel.resultCancelled
        )

        when (widgetId) {
            viewModel.invalidWidgetId ->
                viewEventLiveData.value = ViewEvents.FinishViewInvalidId
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
        viewEventLiveData.value = ViewEvents.SetStateError(viewModel.errorMsg)
        UtilExceptions.throwException(t)
    }


    private fun evalNewData() {
        viewEventLiveData.value = ViewEvents.SetViewData(viewModel.viewData)
        when {
            viewModel.viewData.isEmpty() ->
                viewEventLiveData.value = ViewEvents.SetStateError(viewModel.errorMsgEmptyList)
            else -> viewEventLiveData.value = ViewEvents.SetStateDisplayList
        }
    }


    private fun selectedUserList(position: Int) {
        val userList = viewModel.viewData[position]
        saveDetails(userList.id, userList.title)
        with(viewEventLiveData) {
            value = ViewEvents.SetResults(viewModel.widgetId, viewModel.resultOk)
            value = ViewEvents.FinishView(viewModel.widgetId)
        }
    }

    private fun saveDetails(id: String, title: String) {
        viewEventLiveData.value = ViewEvents.SaveDetails(
                id,
                title,
                viewModel.sharedPrefKeyId,
                viewModel.sharedPrefKeyTitle
        )
    }


    override fun observe(): LiveData<ViewEvents> = viewEventLiveData

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

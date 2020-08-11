package com.precopia.david.lists.view.userlistlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.common.subscribeFlowableUserList
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.common.ListViewLogicBase
import com.precopia.david.lists.view.userlistlist.IUserListViewContract.LogicEvents
import com.precopia.david.lists.view.userlistlist.IUserListViewContract.ViewEvents
import com.precopia.domain.datamodel.UserList
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*

class UserListLogic(private val viewModel: IUserListViewContract.ViewModel,
                    repo: IRepositoryContract.Repository,
                    schedulerProvider: ISchedulerProviderContract,
                    disposable: CompositeDisposable) :
        ListViewLogicBase(repo, schedulerProvider, disposable),
        IUserListViewContract.Logic {

    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(event: LogicEvents) {
        when (event) {
            LogicEvents.OnStart -> onStart()
            is LogicEvents.UserListSelected -> userListSelected(event.position)
            LogicEvents.Add -> add()
            is LogicEvents.Edit -> edit(event.position)
            is LogicEvents.Dragging -> dragging(
                    event.fromPosition, event.toPosition, event.adapter
            )
            is LogicEvents.MovedPermanently -> movedPermanently(event.newPosition)
            is LogicEvents.Delete -> delete(event.position, event.adapter)
            is LogicEvents.UndoRecentDeletion -> undoRecentDeletion(event.adapter)
            LogicEvents.DeletionNotificationTimedOut -> deletionNotificationTimedOut()
            LogicEvents.PreferencesSelected -> preferencesSelected()
        }
    }

    private fun onStart() {
        when {
            viewModel.viewData.isEmpty() -> viewEvent(ViewEvents.SetStateLoading)
            else -> viewEvent(ViewEvents.SetViewData(viewModel.viewData))
        }
        getAllUserLists()
    }


    private fun getAllUserLists() {
        disposable.add(subscribeFlowableUserList(
                repo.getUserLists(),
                { onNextList(it) },
                { onObservableError(it) },
                schedulerProvider
        ))
    }

    private fun onNextList(list: List<UserList>) {
        viewModel.viewData = list.toMutableList()
        evalNewData()
    }

    private fun evalNewData() {
        viewEventLiveData.value = ViewEvents.SetViewData(viewModel.viewData)
        when {
            viewModel.viewData.isEmpty() -> viewEvent(
                    ViewEvents.SetStateError(viewModel.errorMsgEmptyList)
            )
            else -> viewEvent(ViewEvents.SetStateDisplayList)
        }
    }

    private fun onObservableError(t: Throwable) {
        UtilExceptions.throwException(t)
        viewEvent(ViewEvents.SetStateError(viewModel.errorMsg))
    }


    private fun userListSelected(position: Int) {
        if (position < 0) {
            return
        }
        viewEvent(ViewEvents.OpenUserList(viewModel.viewData[position]))
    }


    private fun add() {
        viewEvent(ViewEvents.OpenAddDialog(viewModel.viewData.size))
    }

    private fun edit(position: Int) {
        viewEvent(ViewEvents.OpenEditDialog(viewModel.viewData[position]))
    }


    private fun dragging(fromPosition: Int, toPosition: Int, adapter: IUserListViewContract.Adapter) {
        adapter.move(fromPosition, toPosition)
        Collections.swap(viewModel.viewData, fromPosition, toPosition)
    }

    private fun movedPermanently(newPosition: Int) {
        val userList = viewModel.viewData[newPosition]
        disposable.add(subscribeCompletable(
                repo.updateUserListPosition(userList.id, userList.position, newPosition),
                {},
                { UtilExceptions.throwException(it) },
                schedulerProvider
        ))
    }


    private fun delete(position: Int, adapter: IUserListViewContract.Adapter) {
        adapter.remove(position)
        saveDeletedUserList(position)
        viewEvent(ViewEvents.NotifyUserOfDeletion(viewModel.msgDeletion))
    }

    private fun saveDeletedUserList(position: Int) {
        viewModel.tempList.add(viewModel.viewData[position])
        viewModel.tempPosition = position
        viewModel.viewData.removeAt(position)
    }


    private fun undoRecentDeletion(adapter: IUserListViewContract.Adapter) {
        if (viewModel.tempList.isEmpty() || viewModel.tempPosition < 0) {
            UtilExceptions.throwException(UnsupportedOperationException(
                    viewModel.errorMsgInvalidUndo
            ))
        }
        reAdd(adapter)
        deletionNotificationTimedOut()
    }

    private fun reAdd(adapter: IUserListViewContract.Adapter) {
        val lastDeletedPosition = viewModel.tempList.size - 1
        reAddUserListToAdapter(lastDeletedPosition, adapter)
        reAddUserListToLocalList(lastDeletedPosition)
        viewModel.tempList.removeAt(lastDeletedPosition)
    }

    private fun reAddUserListToAdapter(lastDeletedPosition: Int, adapter: IUserListViewContract.Adapter) {
        adapter.reAdd(
                viewModel.tempPosition,
                viewModel.tempList[lastDeletedPosition]
        )
    }

    private fun reAddUserListToLocalList(lastDeletedPosition: Int) {
        viewModel.viewData.add(
                viewModel.tempPosition,
                viewModel.tempList[lastDeletedPosition]
        )
    }

    private fun deletionNotificationTimedOut() {
        if (viewModel.tempList.isEmpty()) {
            return
        }
        disposable.add(subscribeCompletable(
                repo.deleteUserLists(viewModel.tempList),
                { viewModel.tempList.clear() },
                { deletionError(it) },
                schedulerProvider
        ))
    }

    private fun deletionError(t: Throwable) {
        UtilExceptions.throwException(t)
        viewModel.tempList.clear()
        viewEvent(ViewEvents.ShowMessage(viewModel.errorMsg))
    }


    private fun preferencesSelected() {
        viewEvent(ViewEvents.OpenPreferences)
    }


    private fun viewEvent(event: ViewEvents) {
        viewEventLiveData.value = event
    }

    override fun observe(): LiveData<ViewEvents> = viewEventLiveData


    override fun onCleared() {
        disposable.clear()
    }
}
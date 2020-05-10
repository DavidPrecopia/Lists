package com.precopia.david.lists.view.userlistlist

import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.common.subscribeFlowableUserList
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.IUtilNightModeContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.common.ListViewLogicBase
import com.precopia.domain.datamodel.UserList
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class UserListListLogic(private val view: IUserListViewContract.View,
                        private val viewModel: IUserListViewContract.ViewModel,
                        private val utilNightMode: IUtilNightModeContract,
                        repo: IRepositoryContract.Repository,
                        schedulerProvider: ISchedulerProviderContract,
                        disposable: CompositeDisposable) :
        ListViewLogicBase(repo, schedulerProvider, disposable),
        IUserListViewContract.Logic {


    override val isNightModeEnabled: Boolean
        get() = utilNightMode.nightModeEnabled


    override fun onStart() {
        when {
            viewModel.viewData.isEmpty() -> view.setStateLoading()
            else -> view.setViewData(viewModel.viewData)
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
        view.setViewData(viewModel.viewData)
        when {
            viewModel.viewData.isEmpty() -> view.setStateError(viewModel.errorMsgEmptyList)
            else -> view.setStateDisplayList()
        }
    }

    private fun onObservableError(t: Throwable) {
        UtilExceptions.throwException(t)
        view.setStateError(viewModel.errorMsg)
    }


    override fun userListSelected(position: Int) {
        if (position < 0) {
            return
        }
        view.openUserList(viewModel.viewData[position])
    }


    override fun add() {
        view.openAddDialog(viewModel.viewData.size)
    }

    override fun edit(position: Int) {
        view.openEditDialog(viewModel.viewData[position])
    }


    override fun dragging(fromPosition: Int, toPosition: Int, adapter: IUserListViewContract.Adapter) {
        adapter.move(fromPosition, toPosition)
        Collections.swap(viewModel.viewData, fromPosition, toPosition)
    }

    override fun movedPermanently(newPosition: Int) {
        val userList = viewModel.viewData[newPosition]
        disposable.add(subscribeCompletable(
                repo.updateUserListPosition(userList.id, userList.position, newPosition),
                {},
                { UtilExceptions.throwException(it) },
                schedulerProvider
        ))
    }


    override fun delete(position: Int, adapter: IUserListViewContract.Adapter) {
        adapter.remove(position)
        saveDeletedUserList(position)
        view.notifyUserOfDeletion(viewModel.msgDeletion)
    }

    private fun saveDeletedUserList(position: Int) {
        viewModel.tempList.add(viewModel.viewData[position])
        viewModel.tempPosition = position
        viewModel.viewData.removeAt(position)
    }


    override fun undoRecentDeletion(adapter: IUserListViewContract.Adapter) {
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

    override fun deletionNotificationTimedOut() {
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
        view.showMessage(viewModel.errorMsg)
    }


    override fun preferencesSelected() {
        view.openPreferences()
    }


    override fun setNightMode(isMenuItemChecked: Boolean) {
        when {
            isMenuItemChecked -> utilNightMode.setDay()
            else -> utilNightMode.setNight()
        }
    }


    override fun onDestroy() {
        disposable.clear()
    }
}
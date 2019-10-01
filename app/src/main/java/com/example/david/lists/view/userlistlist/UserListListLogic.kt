package com.example.david.lists.view.userlistlist

import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.IUtilNightModeContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.common.ListViewLogicBase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.DisposableSubscriber
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
        if (viewModel.viewData.isEmpty()) {
            view.setStateLoading()
        } else {
            view.setViewData(viewModel.viewData)
        }
        getAllUserLists()
    }


    private fun getAllUserLists() {
        disposable.add(repo.getUserLists
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribeWith(userListSubscriber())
        )
    }

    private fun userListSubscriber() = object : DisposableSubscriber<List<UserList>>() {
        override fun onNext(userLists: List<UserList>) {
            viewModel.viewData = userLists.toMutableList()
            evalNewData()
        }

        override fun onError(t: Throwable) {
            view.setStateError(viewModel.errorMsg)
        }

        override fun onComplete() {}
    }

    private fun evalNewData() {
        view.setViewData(viewModel.viewData)

        if (viewModel.viewData.isEmpty()) {
            view.setStateError(viewModel.errorMsgEmptyList)
        } else {
            view.setStateDisplayList()
        }
    }


    override fun userListSelected(position: Int) {
        view.openUserList(viewModel.viewData[position])
    }


    override fun add() {
        view.openAddDialog(viewModel.viewData.size)
    }

    override fun edit(position: Int) {
        if (position < 0) {
            UtilExceptions.throwException(IllegalArgumentException())
        }
        view.openEditDialog(viewModel.viewData[position])
    }


    override fun dragging(fromPosition: Int, toPosition: Int, adapter: IUserListViewContract.Adapter) {
        adapter.move(fromPosition, toPosition)
        Collections.swap(viewModel.viewData, fromPosition, toPosition)
    }

    override fun movedPermanently(newPosition: Int) {
        if (newPosition < 0) {
            UtilExceptions.throwException(IllegalArgumentException())
        }
        val userList = viewModel.viewData[newPosition]
        repo.updateUserListPosition(
                userList,
                userList.position,
                newPosition
        )
    }


    override fun delete(position: Int, adapter: IUserListViewContract.Adapter) {
        if (position < 0) {
            UtilExceptions.throwException(IllegalArgumentException())
        }
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
                    viewModel.msgInvalidUndo
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
        repo.deleteUserLists(viewModel.tempList)
        viewModel.tempList.clear()
    }


    override fun signOut() {
        view.confirmSignOut()
    }

    override fun signOutConfirmed() {
        view.signOut(viewModel.signOutResultCode)
    }


    override fun setNightMode(isMenuItemChecked: Boolean) {
        if (isMenuItemChecked) {
            utilNightMode.setDay()
        } else {
            utilNightMode.setNight()
        }
    }


    override fun onDestroy() {
        disposable.clear()
    }
}
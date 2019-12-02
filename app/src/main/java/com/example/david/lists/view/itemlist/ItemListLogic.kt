package com.example.david.lists.view.itemlist

import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.common.ListViewLogicBase
import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList
import com.example.domain.repository.IRepositoryContract
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.DisposableSubscriber
import java.util.*

class ItemListLogic(private val view: IItemViewContract.View,
                    private val viewModel: IItemViewContract.ViewModel,
                    repo: IRepositoryContract.Repository,
                    schedulerProvider: ISchedulerProviderContract,
                    disposable: CompositeDisposable) :
        ListViewLogicBase(repo, schedulerProvider, disposable),
        IItemViewContract.Logic {

    override fun onStart() {
        when {
            viewModel.viewData.isEmpty() -> view.setStateLoading()
            else -> view.setViewData(viewModel.viewData)
        }
        observeDeletedUserLists()
        getItems()
    }


    private fun observeDeletedUserLists() {
        disposable.add(repo.userListDeletedObservable
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribeWith(deletedSubscriber()))
    }

    private fun deletedSubscriber() = object : DisposableSubscriber<List<UserList>>() {
        override fun onNext(userLists: List<UserList>) {
            for ((title, _, id) in userLists) {
                if (id == viewModel.userListId) {
                    view.showMessage(viewModel.getMsgListDeleted(title))
                    view.finishView()
                }
            }
        }

        override fun onError(t: Throwable) {
            UtilExceptions.throwException(t)
        }

        override fun onComplete() {
        }
    }


    private fun getItems() {
        disposable.add(repo.getItems(viewModel.userListId)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribeWith(itemSubscriber())
        )
    }

    private fun itemSubscriber() = object : DisposableSubscriber<List<Item>>() {
        override fun onNext(itemList: List<Item>) {
            viewModel.viewData = itemList.toMutableList()
            evalNewData()
        }

        override fun onError(t: Throwable) {
            view.setStateError(viewModel.errorMsg)
        }

        override fun onComplete() {}
    }

    private fun evalNewData() {
        view.setViewData(viewModel.viewData)
        when {
            viewModel.viewData.isEmpty() -> view.setStateError(viewModel.errorMsgEmptyList)
            else -> view.setStateDisplayList()
        }
    }


    override fun add() {
        view.openAddDialog(viewModel.userListId, viewModel.viewData.size)
    }

    override fun edit(position: Int) {
        view.openEditDialog(viewModel.viewData[position])
    }


    override fun dragging(fromPosition: Int, toPosition: Int, adapter: IItemViewContract.Adapter) {
        Collections.swap(viewModel.viewData, fromPosition, toPosition)
        adapter.move(fromPosition, toPosition)
    }

    override fun movedPermanently(newPosition: Int) {
        val item = viewModel.viewData[newPosition]
        repo.updateItemPosition(
                item,
                item.position,
                newPosition
        )
    }


    override fun delete(position: Int, adapter: IItemViewContract.Adapter) {
        adapter.remove(position)
        saveDeletedItem(position)
        view.notifyUserOfDeletion(viewModel.msgDeletion)
    }

    private fun saveDeletedItem(position: Int) {
        viewModel.tempList.add(viewModel.viewData[position])
        viewModel.tempPosition = position
        viewModel.viewData.removeAt(position)
    }


    override fun undoRecentDeletion(adapter: IItemViewContract.Adapter) {
        if (viewModel.tempList.isEmpty() || viewModel.tempPosition < 0) {
            UtilExceptions.throwException(UnsupportedOperationException(
                    viewModel.errorMsgInvalidUndo
            ))
        }
        reAdd(adapter)
        deletionNotificationTimedOut()
    }

    private fun reAdd(adapter: IItemViewContract.Adapter) {
        val lastDeletedPosition = viewModel.tempList.size - 1
        reAddItemToAdapter(lastDeletedPosition, adapter)
        reAddItemToLocalList(lastDeletedPosition)
        viewModel.tempList.removeAt(lastDeletedPosition)
    }

    private fun reAddItemToAdapter(lastDeletedPosition: Int, adapter: IItemViewContract.Adapter) {
        adapter.reAdd(
                viewModel.tempPosition,
                viewModel.tempList[lastDeletedPosition]
        )
    }

    private fun reAddItemToLocalList(lastDeletedPosition: Int) {
        viewModel.viewData.add(
                viewModel.tempPosition,
                viewModel.tempList[lastDeletedPosition]
        )
    }

    override fun deletionNotificationTimedOut() {
        if (viewModel.tempList.isEmpty()) {
            return
        }
        repo.deleteItems(viewModel.tempList)
        viewModel.tempList.clear()
    }


    override fun onDestroy() {
        disposable.clear()
    }
}

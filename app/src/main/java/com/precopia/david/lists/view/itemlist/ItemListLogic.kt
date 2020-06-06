package com.precopia.david.lists.view.itemlist

import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.common.subscribeFlowableItem
import com.precopia.david.lists.common.subscribeFlowableUserList
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.common.ListViewLogicBase
import com.precopia.domain.datamodel.Item
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable
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
        disposable.add(subscribeFlowableUserList(
                repo.userListDeletedObservable,
                {
                    for ((title, _, id) in it) {
                        if (id == viewModel.userListId) {
                            view.showMessage(viewModel.getMsgListDeleted(title))
                            view.finishView()
                        }
                    }
                },
                { UtilExceptions.throwException(it) },
                schedulerProvider
        ))
    }

    private fun getItems() {
        disposable.add(subscribeFlowableItem(
                repo.getItems(viewModel.userListId),
                { onNextList(it) },
                { onObservableError(it) },
                schedulerProvider
        ))
    }

    private fun onNextList(list: List<Item>) {
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
        disposable.add(subscribeCompletable(
                repo.updateItemPosition(item.id, item.userListId, item.position, newPosition),
                {},
                {
                    view.showMessage(viewModel.errorMsg)
                    UtilExceptions.throwException(it)
                },
                schedulerProvider
        ))
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
        disposable.add(subscribeCompletable(
                repo.deleteItems(viewModel.tempList),
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


    override fun onDestroy() {
        disposable.clear()
    }
}

package com.precopia.david.lists.view.itemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.common.subscribeFlowableItem
import com.precopia.david.lists.common.subscribeFlowableUserList
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.common.ListViewLogicBase
import com.precopia.david.lists.view.itemlist.IItemViewContract.LogicEvents
import com.precopia.david.lists.view.itemlist.IItemViewContract.ViewEvents
import com.precopia.domain.datamodel.Item
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*

class ItemLogic(private val viewModel: IItemViewContract.ViewModel,
                repo: IRepositoryContract.Repository,
                schedulerProvider: ISchedulerProviderContract,
                disposable: CompositeDisposable) :
        ListViewLogicBase(repo, schedulerProvider, disposable),
        IItemViewContract.Logic {


    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(even: LogicEvents) {
        when (even) {
            LogicEvents.OnStart -> onStart()
            LogicEvents.Add -> add()
            is LogicEvents.Edit -> edit(even.position)
            is LogicEvents.Dragging ->
                dragging(even.fromPosition, even.toPosition, even.adapter)
            is LogicEvents.MovedPermanently -> movedPermanently(even.newPosition)
            is LogicEvents.Delete -> delete(even.position, even.adapter)
            is LogicEvents.UndoRecentDeletion -> undoRecentDeletion(even.adapter)
            LogicEvents.DeletionNotificationTimedOut -> deletionNotificationTimedOut()
        }
    }


    private fun onStart() {
        when {
            viewModel.viewData.isEmpty() -> viewEvent(ViewEvents.SetStateLoading)
            else -> viewEvent(ViewEvents.SetViewData(viewModel.viewData))
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
                            viewEvent(ViewEvents.ShowMessage(viewModel.getMsgListDeleted(title)))
                            viewEvent(ViewEvents.FinishView)
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
        viewEvent(ViewEvents.SetViewData(viewModel.viewData))
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


    private fun add() {
        viewEvent(ViewEvents.OpenAddDialog(
                viewModel.userListId, viewModel.viewData.size
        ))
    }

    private fun edit(position: Int) {
        viewEvent(ViewEvents.OpenEditDialog(viewModel.viewData[position]))
    }


    private fun dragging(fromPosition: Int, toPosition: Int, adapter: IItemViewContract.Adapter) {
        Collections.swap(viewModel.viewData, fromPosition, toPosition)
        adapter.move(fromPosition, toPosition)
    }

    private fun movedPermanently(newPosition: Int) {
        val item = viewModel.viewData[newPosition]
        disposable.add(subscribeCompletable(
                repo.updateItemPosition(item.id, item.userListId, item.position, newPosition),
                {},
                {
                    viewEvent(ViewEvents.ShowMessage(viewModel.errorMsg))
                    UtilExceptions.throwException(it)
                },
                schedulerProvider
        ))
    }


    private fun delete(position: Int, adapter: IItemViewContract.Adapter) {
        adapter.remove(position)
        saveDeletedItem(position)
        viewEvent(ViewEvents.NotifyUserOfDeletion(viewModel.msgDeletion))
    }

    private fun saveDeletedItem(position: Int) {
        viewModel.tempList.add(viewModel.viewData[position])
        viewModel.tempPosition = position
        viewModel.viewData.removeAt(position)
    }


    private fun undoRecentDeletion(adapter: IItemViewContract.Adapter) {
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

    private fun deletionNotificationTimedOut() {
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
        viewEvent(ViewEvents.ShowMessage(viewModel.errorMsg))
    }


    private fun viewEvent(event: ViewEvents) {
        viewEventLiveData.value = event
    }

    override fun observe(): LiveData<ViewEvents> = viewEventLiveData


    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}

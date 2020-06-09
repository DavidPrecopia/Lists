package com.precopia.david.lists.view.itemlist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.precopia.david.lists.common.application
import com.precopia.david.lists.common.navigateUp
import com.precopia.david.lists.view.common.ListViewBase
import com.precopia.david.lists.view.itemlist.IItemViewContract.LogicEvents
import com.precopia.david.lists.view.itemlist.IItemViewContract.ViewEvents
import com.precopia.david.lists.view.itemlist.buldlogic.DaggerItemComponent
import com.precopia.domain.datamodel.Item
import javax.inject.Inject

class ItemView : ListViewBase(), IItemViewContract.View {

    @Inject
    lateinit var logic: IItemViewContract.Logic

    @Inject
    lateinit var adapter: IItemViewContract.Adapter

    private val args: ItemViewArgs by navArgs()

    override val title: String
        get() = args.userListTitle


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(logic) {
            onEvent(LogicEvents.OnStart)
            observe().observe(viewLifecycleOwner, Observer { evalViewEvent(it) })
        }
    }

    private fun inject() {
        DaggerItemComponent.builder()
                .application(application)
                .view(this)
                .movementCallback(this)
                .userListId(args.userListId)
                .build()
                .inject(this)
    }


    private fun evalViewEvent(event: ViewEvents) {
        when (event) {
            is ViewEvents.OpenAddDialog -> openAddDialog(event.userListId, event.position)
            is ViewEvents.OpenEditDialog ->  openEditDialog(event.item)
            is ViewEvents.SetViewData -> setViewData(event.viewData)
            is ViewEvents.NotifyUserOfDeletion -> notifyUserOfDeletion(event.message)
            ViewEvents.SetStateDisplayList -> setStateDisplayList()
            ViewEvents.SetStateLoading -> setStateLoading()
            is ViewEvents.SetStateError -> setStateError(event.message)
            is ViewEvents.ShowMessage -> showMessage(event.message)
            ViewEvents.FinishView -> finishView()
        }
    }


    private fun openAddDialog(userListId: String, position: Int) {
        findNavController().navigate(
                ItemViewDirections.actionItemListViewToAddEditItemDialog(
                        "", "", userListId, position
                )
        )
    }

    private fun openEditDialog(item: Item) {
        findNavController().navigate(
                ItemViewDirections.actionItemListViewToAddEditItemDialog(
                        item.id, item.title, item.userListId, item.position
                )
        )
    }


    private fun setViewData(viewData: List<Item>) {
        adapter.setData(viewData)
    }


    private fun notifyUserOfDeletion(message: String) {
        super.notifyDeletionSnackbar(message)
    }

    private fun setStateDisplayList() {
        super.displayList()
    }

    private fun setStateLoading() {
        super.displayLoading()
    }

    private fun setStateError(message: String) {
        super.displayError(message)
    }

    private fun showMessage(message: String) {
        super.toastMessage(message)
    }

    private fun finishView() {
        navigateUp()
    }


    override fun addButtonClicked() {
        logic.onEvent(LogicEvents.Add)
    }

    override fun undoRecentDeletion() {
        logic.onEvent(LogicEvents.UndoRecentDeletion(adapter))
    }

    override fun deletionNotificationTimedOut() {
        logic.onEvent(LogicEvents.DeletionNotificationTimedOut)
    }

    override fun draggingListItem(fromPosition: Int, toPosition: Int) {
        logic.onEvent(LogicEvents.Dragging(fromPosition, toPosition, adapter))
    }

    override fun permanentlyMoved(newPosition: Int) {
        logic.onEvent(LogicEvents.MovedPermanently(newPosition))
    }

    override fun enableUpNavigationOnToolbar() = true

    override fun getAdapter() = adapter as RecyclerView.Adapter<*>
}

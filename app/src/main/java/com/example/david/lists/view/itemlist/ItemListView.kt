package com.example.david.lists.view.itemlist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.view.common.ListViewBase
import com.example.david.lists.view.itemlist.buldlogic.DaggerItemListViewComponent
import javax.inject.Inject

class ItemListView : ListViewBase(), IItemViewContract.View {

    @Inject
    lateinit var logic: IItemViewContract.Logic

    @Inject
    lateinit var adapter: IItemViewContract.Adapter

    private val args: ItemListViewArgs by navArgs()

    override val title: String
        get() = args.userListTitle


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logic.onStart()
    }

    private fun inject() {
        DaggerItemListViewComponent.builder()
                .application(activity!!.application)
                .view(this)
                .movementCallback(this)
                .userListId(args.userListId)
                .build()
                .inject(this)
    }


    override fun openAddDialog(userListId: String, position: Int) {
        findNavController().navigate(
                ItemListViewDirections.actionItemListViewToAddEditItemDialog(
                        "", "", userListId, position
                )
        )
    }

    override fun openEditDialog(item: Item) {
        findNavController().navigate(
                ItemListViewDirections.actionItemListViewToAddEditItemDialog(
                        item.id, item.title, item.userListId, item.position
                )
        )
    }


    override fun setViewData(viewData: List<Item>) {
        adapter.setData(viewData)
    }


    override fun notifyUserOfDeletion(message: String) {
        super.notifyDeletionSnackbar(message)
    }

    override fun setStateDisplayList() {
        super.displayList()
    }

    override fun setStateLoading() {
        super.displayLoading()
    }

    override fun setStateError(message: String) {
        super.displayError(message)
    }

    override fun showMessage(message: String) {
        super.toastMessage(message)
    }

    override fun finishView() {
        activity!!.finish()
    }


    override fun onDestroy() {
        logic.onDestroy()
        super.onDestroy()
    }

    override fun addButtonClicked() {
        logic.add()
    }

    override fun undoRecentDeletion() {
        logic.undoRecentDeletion(adapter)
    }

    override fun deletionNotificationTimedOut() {
        logic.deletionNotificationTimedOut()
    }

    override fun draggingListItem(fromPosition: Int, toPosition: Int) {
        logic.dragging(fromPosition, toPosition, adapter)
    }

    override fun permanentlyMoved(newPosition: Int) {
        logic.movedPermanently(newPosition)
    }

    override fun enableUpNavigationOnToolbar() = true

    override fun getAdapter() = adapter as RecyclerView.Adapter<*>
}

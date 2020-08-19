package com.precopia.david.lists.view.userlistlist

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.precopia.david.lists.R
import com.precopia.david.lists.common.application
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.common.ListViewBase
import com.precopia.david.lists.view.userlistlist.IUserListViewContract.LogicEvents
import com.precopia.david.lists.view.userlistlist.IUserListViewContract.ViewEvents
import com.precopia.david.lists.view.userlistlist.buldlogic.DaggerUserListComponent
import com.precopia.domain.datamodel.UserList
import javax.inject.Inject

class UserListView : ListViewBase(),
        IUserListViewContract.View {

    @Inject
    lateinit var logic: IUserListViewContract.Logic

    @Inject
    lateinit var adapter: IUserListViewContract.Adapter

    override val title: String
        get() = getString(R.string.app_name)


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerUserListComponent.builder()
                .application(application)
                .view(this)
                .movementCallback(this)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(logic) {
            onEvent(LogicEvents.OnStart)
            observe().observe(viewLifecycleOwner, Observer { evalViewEvents(it) })
        }
    }

    private fun evalViewEvents(event: ViewEvents) {
        when (event) {
            is ViewEvents.OpenUserList -> openUserList(event.userList)
            ViewEvents.OpenPreferences -> openPreferences()
            is ViewEvents.OpenAddDialog -> openAddDialog(event.position)
            is ViewEvents.OpenEditDialog -> openEditDialog(event.userList)
            is ViewEvents.SetViewData -> setViewData(event.viewData)
            is ViewEvents.NotifyUserOfDeletion -> notifyUserOfDeletion(event.message)
            ViewEvents.SetStateDisplayList -> setStateDisplayList()
            ViewEvents.SetStateLoading -> setStateLoading()
            is ViewEvents.SetStateError -> setStateError(event.message)
            is ViewEvents.ShowMessage -> showMessage(event.message)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_id_preferences -> logic.onEvent(LogicEvents.PreferencesSelected)
            else -> UtilExceptions.throwException(IllegalArgumentException())
        }
        return super.onOptionsItemSelected(item)
    }


    private fun openUserList(userList: UserList) {
        findNavController().navigate(UserListViewDirections.actionUserListListViewToItemListView(
                userList.id,
                userList.title
        ))
    }


    private fun openPreferences() {
        findNavController().navigate(
                UserListViewDirections.actionUserListListViewToPreferencesView()
        )
    }


    private fun openAddDialog(position: Int) {
        findNavController().navigate(
                UserListViewDirections.actionUserListListViewToAddEditUserListDialog(
                        "", "", position
                )
        )
    }

    private fun openEditDialog(userList: UserList) {
        findNavController().navigate(
                UserListViewDirections.actionUserListListViewToAddEditUserListDialog(
                        userList.id, userList.title, userList.position
                )
        )
    }

    private fun setViewData(viewData: List<UserList>) {
        adapter.setData(viewData)
    }

    private fun notifyUserOfDeletion(message: String) {
        notifyDeletionSnackbar(message)
    }


    private fun setStateDisplayList() {
        displayList()
    }

    private fun setStateLoading() {
        displayLoading()
    }

    private fun setStateError(message: String) {
        displayError(message)
    }


    private fun showMessage(message: String) {
        super.toastMessage(message)
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

    override fun enableUpNavigationOnToolbar() = false

    override fun getAdapter() = adapter as RecyclerView.Adapter<*>
}

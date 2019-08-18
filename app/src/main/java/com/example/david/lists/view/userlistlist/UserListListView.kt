package com.example.david.lists.view.userlistlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.david.lists.R
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.addedit.userlist.AddEditUserListDialog
import com.example.david.lists.view.authentication.AuthView
import com.example.david.lists.view.authentication.IAuthContract
import com.example.david.lists.view.common.ListViewBase
import com.example.david.lists.view.itemlist.ItemActivity
import com.example.david.lists.view.userlistlist.buldlogic.DaggerUserListListViewComponent
import org.jetbrains.anko.intentFor
import javax.inject.Inject

class UserListListView : ListViewBase(), IUserListViewContract.View, ConfirmSignOutDialog.ConfirmSignOutCallback {

    @Inject
    lateinit var logic: IUserListViewContract.Logic

    @Inject
    lateinit var adapter: IUserListViewContract.Adapter

    private var authRequestCode: Int = 0
    private var intentExtraAuthResultKey: String? = null

    override val title: String
        get() = getString(R.string.app_name)


    companion object {
        fun newInstance() = UserListListView()
    }

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerUserListListViewComponent.builder()
                .application(activity!!.application)
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
        logic.onStart()
    }

    override fun onDestroy() {
        logic.onDestroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflateMenu(menu, inflater)
        initMenuSetCheckedState(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(
                if (logic.isUserAnon) R.menu.menu_sign_in else R.menu.menu_sign_out,
                menu
        )
    }

    private fun initMenuSetCheckedState(menu: Menu) {
        menu.findItem(R.id.menu_id_night_mode).isChecked = logic.isNightModeEnabled
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_id_sign_out -> logic.signOut()
            R.id.menu_id_sign_in -> logic.signIn()
            R.id.menu_id_night_mode -> {
                with(item.isChecked) {
                    logic.setNightMode(this)
                    this.not()
                }
            }
            else -> UtilExceptions.throwException(IllegalArgumentException())
        }
        return super.onOptionsItemSelected(item)
    }


    override fun openUserList(userList: UserList) {
        startActivity(context!!.intentFor<ItemActivity>(
                getString(R.string.intent_extra_user_list_id) to userList.id,
                getString(R.string.intent_extra_user_list_title) to userList.title
        ))
    }


    override fun confirmSignOut() {
        openDialogFragment(ConfirmSignOutDialog(this))
    }

    override fun signOutConfirmed() {
        logic.signOutConfirmed()
    }

    override fun openAuthentication(authGoal: IAuthContract.AuthGoal, requestCode: Int, intentExtraAuthResultKey: String) {
        this.authRequestCode = requestCode
        this.intentExtraAuthResultKey = intentExtraAuthResultKey

        val intent = context!!.intentFor<AuthView>(
                getString(R.string.intent_extra_auth) to authGoal
        )
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != authRequestCode) {
            return
        }
        logic.authResult(getIntentExtra(data!!))
    }

    private fun getIntentExtra(data: Intent): IAuthContract.AuthResult {
        return data.getSerializableExtra(intentExtraAuthResultKey) as IAuthContract.AuthResult
    }


    override fun openAddDialog(position: Int) {
        openDialogFragment(AddEditUserListDialog.getInstance(
                "", "", position
        ))
    }

    override fun openEditDialog(userList: UserList) {
        openDialogFragment(AddEditUserListDialog.getInstance(
                userList.id, userList.title, userList.position
        ))
    }

    override fun setViewData(viewData: List<UserList>) {
        adapter.setData(viewData)
    }

    override fun notifyUserOfDeletion(message: String) {
        notifyDeletionSnackbar(message)
    }


    override fun setStateDisplayList() {
        displayList()
    }

    override fun setStateLoading() {
        displayLoading()
    }

    override fun setStateError(message: String) {
        displayError(message)
    }

    override fun recreateView() {
        activity!!.recreate()
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

    override fun enableUpNavigationOnToolbar() = false

    override fun getAdapter() = adapter as RecyclerView.Adapter<*>
}

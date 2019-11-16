package com.example.david.lists.view.common

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.david.lists.R
import com.example.david.lists.common.toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.Callback.*
import kotlinx.android.synthetic.main.list_view_base.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject
import javax.inject.Provider

abstract class ListViewBase : Fragment(R.layout.list_view_base),
        TouchHelperCallback.MovementCallback {

    @Inject
    lateinit var layoutManger: Provider<LinearLayoutManager>

    @Inject
    lateinit var dividerItemDecorator: RecyclerView.ItemDecoration

    @Inject
    lateinit var itemTouchHelper: ItemTouchHelper

    protected abstract val title: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    protected abstract fun addButtonClicked()

    protected abstract fun undoRecentDeletion()

    protected abstract fun deletionNotificationTimedOut()

    protected abstract fun draggingListItem(fromPosition: Int, toPosition: Int)

    protected abstract fun permanentlyMoved(newPosition: Int)

    protected abstract fun enableUpNavigationOnToolbar(): Boolean

    protected abstract fun getAdapter(): RecyclerView.Adapter<*>


    private fun init() {
        initRecyclerView()
        initToolbar()
        initFab()
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = layoutManger.get()
            addItemDecoration(dividerItemDecorator)
            itemTouchHelper.attachToRecyclerView(this)
            adapter = this@ListViewBase.getAdapter()
        }
    }

    private fun initToolbar() {
        with(toolbar) {
            (activity as AppCompatActivity).setSupportActionBar(this)
            title = this@ListViewBase.title
            if (enableUpNavigationOnToolbar()) {
                setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
                setNavigationOnClickListener { findNavController().navigateUp() }
            }
        }
    }

    private fun initFab() {
        fab.setOnClickListener { addButtonClicked() }
        fabScrollListener()
    }

    private fun fabScrollListener() {
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    fab.hide()
                } else if (dy < 0) {
                    fab.show()
                }
            }
        })
    }

    protected fun notifyDeletionSnackbar(message: String) {
        Snackbar.make(root_layout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.msg_undo) { undoRecentDeletion() }
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (validSnackbarEvent(event)) {
                            deletionNotificationTimedOut()
                        }
                    }
                })
                .show()
    }

    private fun validSnackbarEvent(event: Int) =
            event == DISMISS_EVENT_TIMEOUT
                    || event == DISMISS_EVENT_SWIPE
                    || event == DISMISS_EVENT_MANUAL


    override fun dragging(fromPosition: Int, toPosition: Int) {
        draggingListItem(fromPosition, toPosition)
    }

    override fun movedPermanently(newPosition: Int) {
        permanentlyMoved(newPosition)
    }


    /**
     * Because I am not using LiveData, the Logic classes will attempt to
     * manipulate the UI when the View is in an invalid state to do so.
     */
    private fun validLifecycleState() =
            lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

    protected fun displayLoading() {
        if (validLifecycleState()) {
            tv_error.isGone = true
            recycler_view.isGone = true
            fab.hide()

            progress_bar.isVisible = true
        }
    }

    protected fun displayList() {
        if (validLifecycleState()) {
            progress_bar.isGone = true
            tv_error.isGone = true

            recycler_view.isVisible = true
            fab.show()
        }
    }

    protected fun displayError(errorMessage: String) {
        if (validLifecycleState()) {
            progress_bar?.isGone = true
            recycler_view?.isGone = true
            fab?.show()

            tv_error?.text = errorMessage
            tv_error?.isVisible = true
        }
    }


    protected fun toastMessage(message: String) {
        toast(message)
    }
}

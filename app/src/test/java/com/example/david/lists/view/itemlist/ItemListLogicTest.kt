package com.example.david.lists.view.itemlist

import com.example.david.lists.SchedulerProviderMockInit
import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.data.repository.IRepositoryContract.Repository
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.itemlist.IItemViewContract.Adapter
import com.example.david.lists.view.itemlist.IItemViewContract.View
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.contains
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ItemListLogicTest {

    @Mock
    private lateinit var view: View

    @Mock
    private lateinit var viewModel: IItemViewContract.ViewModel

    @Mock
    private lateinit var repo: Repository

    @Mock
    private lateinit var schedulerProvider: ISchedulerProviderContract

    @Spy
    private lateinit var disposable: CompositeDisposable


    @InjectMocks
    private lateinit var logic: ItemListLogic


    @Mock
    private lateinit var adapter: Adapter

    private val userListId = "qwerty"

    private val id = "id_one"
    private val title = "title_one"
    private val position = 0
    private val item = Item(title, position, userListId, id)

    private val idTwo = "idTwo"
    private val titleTwo = "titleTwo"
    private val positionTwo = 1
    private val itemTwo = Item(titleTwo, positionTwo, userListId, idTwo)

    private val itemList = ArrayList(listOf(item))

    private val invalidPosition = -1
    private val errorMsg = "error"


    @Before
    fun setUp() {
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    /**
     * Normal behavior
     * - Set the View's data because the ViewModel's view data is not empty.
     * - Observe deleted UserLists Flowable from repo.
     * - Get List from repo.
     * - Save List to ViewModel.
     * - Submit the List to the View.
     * - Set View state display list.
     */
    @Test
    fun onStart() {
        `when`(viewModel.userListId).thenReturn(userListId)
        `when`(viewModel.viewData).thenReturn(itemList)
        `when`(repo.userListDeletedObservable).thenReturn(Flowable.just(ArrayList()))
        `when`(repo.getItems(userListId)).thenReturn(Flowable.just(itemList))

        logic.onStart()

        verify(viewModel).viewData = itemList
        verify(view, times(2)).setViewData(itemList)
        verify(view).setStateDisplayList()
    }

    /**
     * Error behavior - empty List from repo,
     * - Set View state loading.
     * - Observe deleted UserLists Flowable from repo.
     * - Get List from repo.
     * - Save List to ViewModel.
     * - Submit the List to the View.
     * - Set View state error with message from the ViewModel.
     */
    @Test
    fun onStartEmptyList() {
        val emptyList = mutableListOf<Item>()

        `when`(viewModel.userListId).thenReturn(userListId)
        `when`(viewModel.viewData).thenReturn(emptyList)
        `when`(viewModel.errorMsgEmptyList).thenReturn(errorMsg)
        `when`(repo.userListDeletedObservable).thenReturn(Flowable.just(ArrayList()))
        `when`(repo.getItems(userListId)).thenReturn(Flowable.just(emptyList))

        logic.onStart()

        verify(view).setStateLoading()
        verify(viewModel).viewData = emptyList
        verify(view).setViewData(emptyList)
        verify(view).setStateError(errorMsg)
    }

    /**
     * Error behavior - repo throws an error,
     * - Set View state loading.
     * - Observe deleted UserLists Flowable from repo.
     * - Get List from repo.
     * - Repo throws an error.
     * - Set View state error with message from the ViewModel.
     */
    @Test
    fun onStartRepoThrowsError() {
        val throwable = Throwable()

        `when`(viewModel.userListId).thenReturn(userListId)
        `when`(viewModel.errorMsg).thenReturn(errorMsg)
        `when`(repo.userListDeletedObservable).thenReturn(Flowable.just(ArrayList()))
        `when`(repo.getItems(userListId)).thenReturn(Flowable.error(throwable))

        logic.onStart()

        verify(view).setStateLoading()
        verify(view).setStateError(errorMsg)
    }


    /**
     * Normal behavior
     * - Invoke [View.openAddDialog] with the
     * UserListId and the view data's List size.
     */
    @Test
    fun add() {
        `when`(viewModel.userListId).thenReturn(userListId)
        `when`(viewModel.viewData).thenReturn(itemList)

        logic.add()

        verify(view).openAddDialog(userListId, itemList.size)
    }


    /**
     * Normal behavior
     * - Get the Item at the passed-in position.
     * - Invoke [View.openEditDialog].
     */
    @Test
    fun edit() {
        val editPosition = 0
        val editItems = mutableListOf(item)

        `when`(viewModel.viewData).thenReturn(editItems)

        logic.edit(editPosition)

        verify(view).openEditDialog(item)
    }

    /**
     * Error behavior - invalid position,
     * - Throw an Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun editInvalidPosition() {
        logic.edit(invalidPosition)
    }


    /**
     * Normal behavior
     * - Update the Adapter with [Adapter.move].
     * - Update the view data.
     */
    @Test
    fun dragging() {
        val fromPos = 0
        val toPos = 1

        val draggingItems = mutableListOf(item, itemTwo)

        `when`(viewModel.viewData).thenReturn(draggingItems)

        logic.dragging(fromPos, toPos, adapter)

        verify(adapter).move(fromPos, toPos)
        assertThat<List<Item>>(draggingItems, `is`(contains(itemTwo, item)))
    }


    /**
     * Normal behavior
     * - Gets Item at passed-in position.
     * - Invokes [Repository.updateItemPosition]
     */
    @Test
    fun movedPermanently() {
        val newPosition = 0
        val moveItems = mutableListOf(item)

        `when`(viewModel.viewData).thenReturn(moveItems)

        logic.movedPermanently(newPosition)

        verify(repo).updateItemPosition(item, item.position, newPosition)
    }

    /**
     * Error behavior - invalid position,
     * - Throws an Exception
     */
    @Test(expected = IllegalArgumentException::class)
    fun movedPermanentlyInvalidPosition() {
        logic.movedPermanently(invalidPosition)
    }


    /**
     * Normal behavior,
     * - Remove from the Adapter.
     * - Save the deleted UserList itself to the ViewModel's temp list.
     * - Save the deleted UserList's position to the ViewModel.
     * - Remove from the view data in the ViewModel.
     * - Notify user of deletion.
     */
    @Test
    fun delete() {
        val deletedPosition = 0
        val deletedItems = mutableListOf(item)
        val tempList = mutableListOf<Item>()

        `when`(viewModel.viewData).thenReturn(deletedItems)
        `when`(viewModel.tempList).thenReturn(tempList)
        `when`(viewModel.msgItemDeleted).thenReturn(errorMsg)

        logic.delete(deletedPosition, adapter)

        verify(adapter).remove(position)
        verify(viewModel).tempPosition = deletedPosition
        verify(view).notifyUserOfDeletion(errorMsg)
        assertThat(deletedItems.isEmpty(), `is`(true))
        assertThat<List<Item>>(tempList, `is`(contains(item)))
    }

    /**
     * Error behavior - invalid position,
     * - Throws an exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun deleteInvalidPosition() {
        logic.delete(invalidPosition, adapter)
    }


    /**
     * Normal behavior
     * - Re-add to the adapter.
     * - Re-add to the view data List.
     * - Remove the last Item from the temp List.
     * - Because the temp List is not empty, pass the temp List to the repo for deletion.
     * - Clear the temp List.
     */
    @Test
    fun undoRecentDeletion() {
        val undoPosition = 0
        val tempList = mutableListOf(item, itemTwo)
        val viewDataList = mutableListOf<Item>()

        `when`(viewModel.tempList).thenReturn(tempList)
        `when`(viewModel.tempPosition).thenReturn(undoPosition)
        `when`(viewModel.viewData).thenReturn(viewDataList)

        logic.undoRecentDeletion(adapter)

        verify(adapter).reAdd(undoPosition, itemTwo)
        verify(repo).deleteItems(tempList)
        assertThat<List<Item>>(viewDataList, `is`(contains(itemTwo)))
        assertThat(tempList.isEmpty(), `is`(true))
    }

    /**
     * Error behavior - empty temp List,
     * - Throws an Exception.
     */
    @Test(expected = UnsupportedOperationException::class)
    fun undoRecentDeletionEmptyTempList() {
        `when`(viewModel.tempList).thenReturn(ArrayList())

        logic.undoRecentDeletion(adapter)
    }

    /**
     * Error behavior - invalid temp position,
     * - Throws an Exception.
     */
    @Test(expected = UnsupportedOperationException::class)
    fun undoRecentDeletionInvalidTempPosition() {
        `when`(viewModel.tempList).thenReturn(itemList)
        `when`(viewModel.tempPosition).thenReturn(invalidPosition)

        logic.undoRecentDeletion(adapter)
    }


    /**
     * Normal behavior
     * - Check if the temp List is empty.
     * - Pass the temp List [Repository.deleteItems].
     * - Clear the temp List.
     */
    @Test
    fun deletionNotificationTimedOut() {
        `when`(viewModel.tempList).thenReturn(itemList)

        logic.deletionNotificationTimedOut()

        verify(repo).deleteItems(itemList)
        assertThat(itemList.isEmpty(), `is`(true))
    }

    /**
     * Error behavior - empty temp List,
     * - Method returns without invoking the repo.
     */
    @Test
    fun deletionNotificationTimedOutEmptyTempList() {
        `when`(viewModel.tempList).thenReturn(ArrayList())

        logic.deletionNotificationTimedOut()

        verify(repo, never()).deleteItems(anyList())
    }


    /**
     * Normal behavior
     * - Verify [CompositeDisposable.clear] is invoked.
     */
    @Test
    fun onDestroy() {
        logic.onDestroy()
        verify(disposable).clear()
    }
}
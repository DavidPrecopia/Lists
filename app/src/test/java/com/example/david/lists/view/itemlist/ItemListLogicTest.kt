package com.example.david.lists.view.itemlist

import com.example.david.lists.SchedulerProviderMockInit
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList
import com.example.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ItemListLogicTest {

    private val view = mockk<IItemViewContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IItemViewContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val disposable = spyk<CompositeDisposable>()


    private val logic = ItemListLogic(view, viewModel, repo, schedulerProvider, disposable)


    private val adapter = mockk<IItemViewContract.Adapter>(relaxUnitFun = true)


    private val id = "id"
    private val title = "title"
    private val position = 0
    private val userListId = "qwerty"
    private val itemOne = Item(title, position, userListId, id)
    private val itemTwo = Item(title + 2, position + 1, userListId, id + 2)

    private val message = "message"

    private val emptyList = mutableListOf<Item>()


    @BeforeEach
    fun init() {
        clearAllMocks()

        SchedulerProviderMockInit.init(schedulerProvider)

        every { viewModel.userListId } returns userListId
        every { repo.userListDeletedObservable } returns Flowable.just(emptyList())
    }


    @Nested
    inner class OnStart {
        /**
         * - Set the View's data with data from the ViewModel - because the ViewModel's view data is not empty.
         * - Get List from repo.
         * - Save List to ViewModel.
         * - Set the View's data
         * - Set View state display list.
         */
        @Test
        fun `onStart - ViewModel has View Data`() {
            val itemList = mutableListOf(itemOne)

            every { repo.getItems(userListId) } returns Flowable.just(itemList)
            every { viewModel.viewData } returns itemList

            logic.onStart()

            verify(exactly = 2) { view.setViewData(itemList) }
            verify { viewModel.viewData = itemList }
            verify { view.setStateDisplayList() }
        }

        /**
         * - Set View state loading.
         *   - Because the ViewModel's ViewData is empty.
         * - Get List from repo.
         * - Save List to ViewModel.
         * - Submit the List to the View.
         * - Set View state error with message from the ViewModel.
         */
        @Test
        fun `onStart - Empty List from repo`() {
            every { repo.getItems(userListId) } returns Flowable.just(emptyList)
            every { viewModel.viewData } returns emptyList
            every { viewModel.errorMsgEmptyList } returns message

            logic.onStart()

            verify { view.setStateLoading() }
            verify { viewModel.viewData = emptyList }
            verify { view.setViewData(emptyList) }
            verify { view.setStateError(message) }
        }

        /**
         * - Set View state loading.
         *   - Because the ViewModel's ViewData is empty.
         * - Get List from repo.
         * - Repo throws an error.
         * - Set View state error with message from ViewModel.
         */
        @Test
        fun `onStart - Repo throws an error`() {
            val throwable = Throwable()

            every { viewModel.viewData } returns emptyList
            every { repo.getItems(userListId) } returns Flowable.error(throwable)
            every { viewModel.errorMsg } returns message

            logic.onStart()

            verify { view.setStateLoading() }
            verify { view.setStateError(message) }
        }
    }


    @Nested
    inner class ObserveDeletedUserLists {
        /**
         * - Observe the observer from the Repo.
         * - Check each UserList's ID against the UserListId in the ViewModel.
         * - If they are equal - they will in this test - display a message and finish the View.
         */
        @Test
        fun `observeDeletedUserLists - UserListId matches`() {
            val userListList = listOf(UserList(title, position, userListId))
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList
            every { repo.getItems(userListId) } returns Flowable.just(itemList)
            every { repo.userListDeletedObservable } returns Flowable.just(userListList)
            every { viewModel.getMsgListDeleted(title) } returns message

            logic.onStart()

            verify { view.showMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - Observe the observer from the Repo.
         * - Check each UserList's ID against the UserListId in the ViewModel.
         * - In this test they will not be equal, thus nothing will happen.
         */
        @Test
        fun `observeDeletedUserLists - UserListId does not match`() {
            val userListIdShouldNotMatch = ""
            val userListList = listOf(UserList(title, position, userListIdShouldNotMatch))
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList
            every { repo.getItems(any()) } returns Flowable.just(itemList)
            every { repo.userListDeletedObservable } returns Flowable.just(userListList)
            every { viewModel.getMsgListDeleted(title) } returns message

            logic.onStart()

            verify(exactly = 0) { view.showMessage(message) }
            verify(exactly = 0) { view.finishView() }
        }
    }


    /**
     * - [IItemViewContract.View.openAddDialog] is invoked with the current size.
     * of List containing the view data.
     */
    @Test
    fun add() {
        val itemList = mutableListOf(itemOne)
        val size = itemList.size

        every { viewModel.viewData } returns itemList

        logic.add()

        verify { view.openAddDialog(userListId, size) }
    }


    @Nested
    inner class Edit {
        /**
         * - Get the Item at the passed-in position.
         * - Pass that Item to [IItemViewContract.View.openEditDialog].
         */
        @Test
        fun edit() {
            val position = 0
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList

            logic.edit(position)

            verify { view.openEditDialog(itemOne) }
        }

        /**
         * - Attempt to get the selected UserList from the ViewModel with the invalid position.
         * - Exception is thrown.
         */
        @Test
        fun `edit - Invalid Position`() {
            val invalidPosition = -1
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList

            assertThrows<IndexOutOfBoundsException> {
                logic.edit(invalidPosition)
            }
        }
    }


    @Nested
    inner class Dragging {
        /**
         * - Updated the Adapter with [IItemViewContract.Adapter.move].
         * - Update the ViewModel's view data - verify that the order has changed.
         */
        @Test
        fun dragging() {
            val fromPosition = 0
            val toPosition = 1
            val itemList = mutableListOf(itemOne, itemTwo)

            every { viewModel.viewData } returns itemList

            logic.dragging(fromPosition, toPosition, adapter)

            verify { adapter.move(fromPosition, toPosition) }
            // Assert that the ViewData was updated.
            assertThat(itemList).containsExactly(itemTwo, itemOne)
        }

        /**
         * - Attempt to re-arrange the ViewData with the invalid positions.
         * - Exception is thrown.
         */
        @ParameterizedTest
        @CsvSource("-1, 0", "0, -1", "-1, -1")
        fun `dragging - Invalid positions`(fromPosition: Int, toPosition: Int) {
            val itemList = mutableListOf(itemOne, itemTwo)

            every { viewModel.viewData } returns itemList

            assertThrows<IndexOutOfBoundsException> {
                logic.dragging(fromPosition, toPosition, adapter)
            }
        }
    }


    @Nested
    inner class MovePermanently {
        /**
         * - Get the moved Item from the ViewModel.
         * - Invoke [IRepositoryContract.Repository.updateItemPosition].
         */
        @Test
        fun movedPermanently() {
            val newPosition = 0
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList

            logic.movedPermanently(newPosition)

            verify { repo.updateItemPosition(itemOne, itemOne.position, newPosition) }
        }

        /**
         * - Attempt to get the selected Item from the ViewModel with the invalid position.
         * - Exception is thrown.
         */
        @Test
        fun `movedPermanently - InvalidPosition`() {
            val invalidPosition = -1
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList

            assertThrows<IndexOutOfBoundsException> {
                logic.movedPermanently(invalidPosition)
            }
        }
    }


    @Nested
    inner class Delete {
        /**
         * - Get the deleted Item from the ViewModel.
         * - Remove from the Adapter.
         * - Save the deleted Item itself to the ViewModel's temp list.
         * - Save the deleted Item's position to the ViewModel.
         * - Remove from the view data in the ViewModel.
         * - Notify user of deletion.
         */
        @Test
        fun delete() {
            val position = 0
            val itemList = mutableListOf(itemOne)
            val tempItemLists = mutableListOf<Item>()

            every { viewModel.viewData } returns itemList
            every { viewModel.tempList } returns tempItemLists
            every { viewModel.msgDeletion } returns message

            logic.delete(position, adapter)

            verify { adapter.remove(position) }
            assertThat(tempItemLists).containsExactly(itemOne)
            verify { viewModel.tempPosition = position }
            assertThat(itemList.isEmpty()).isTrue()
            verify { view.notifyUserOfDeletion(message) }
        }

        /**
         * - Attempt to get the selected Item from the ViewModel with the invalid position.
         * - Exception is thrown
         */
        @Test
        fun `delete - Invalid Position`() {
            val invalidPosition = -1
            val itemList = mutableListOf(itemOne)
            val tempItemLists = mutableListOf<Item>()

            every { viewModel.viewData } returns itemList
            every { viewModel.tempList } returns tempItemLists

            assertThrows<java.lang.IndexOutOfBoundsException> {
                logic.delete(invalidPosition, adapter)
            }
        }
    }


    @Nested
    inner class UndoDeletion {
        /**
         * - Get the last added Item from the ViewModel's temp list.
         * - Re-add to the adapter using the temp position from the ViewModel.
         * - Re-add to the ViewData List.
         * - Remove the last Item from the temp List.
         * - Because the temp List is not empty, pass the temp List to the repo for deletion.
         * - Clear the temp List.
         */
        @Test
        fun undoRecentDeletion() {
            val itemList = mutableListOf<Item>()
            val tempItemList = mutableListOf(itemOne, itemTwo)
            val tempPosition = 0

            every { viewModel.viewData } returns itemList
            every { viewModel.tempList } returns tempItemList
            every { viewModel.tempPosition } returns tempPosition

            logic.undoRecentDeletion(adapter)

            verify { adapter.reAdd(tempPosition, itemTwo) }
            assertThat(itemList).containsExactly(itemTwo)
            verify { repo.deleteItems(tempItemList) }
            assertThat(tempItemList.isEmpty()).isTrue()
        }

        /**
         * - The temp List in the ViewModel is empty.
         * - Exception is thrown
         */
        @Test
        fun `undoRecentDeletion - Empty Temp List`() {
            every { viewModel.tempList } returns emptyList
            every { viewModel.errorMsgInvalidUndo } returns message

            assertThrows<UnsupportedOperationException> {
                logic.undoRecentDeletion(adapter)
            }
        }

        /**
         * - The temp position in the ViewModel is invalid.
         * - Exception is thrown
         */
        @Test
        fun `undoRecentDeletion - Invalid Temp Position`() {
            val invalidPosition = -1
            val tempItemLists = mutableListOf(itemOne)

            every { viewModel.tempList } returns tempItemLists
            every { viewModel.tempPosition } returns invalidPosition
            every { viewModel.errorMsgInvalidUndo } returns message

            assertThrows<UnsupportedOperationException> {
                logic.undoRecentDeletion(adapter)
            }
        }
    }


    @Nested
    inner class DeleteNotificationTimeOut {
        /**
         * - Check if the temp List is empty.
         *   - Will not be in this test.
         * - Pass the temp List [IRepositoryContract.Repository.deleteItems].
         * - Clear the temp List.
         */
        @Test
        fun deletionNotificationTimedOut() {
            val itemList = mutableListOf(itemOne)

            every { viewModel.tempList } returns itemList

            logic.deletionNotificationTimedOut()

            verify { repo.deleteItems(itemList) }
            assertThat(itemList.isEmpty()).isTrue()
        }

        /**
         * - Check if the temp List is empty.
         * - Return without invoking any other method.
         */
        @Test
        fun `deletionNotificationTimedOut - Empty Temp List`() {
            every { viewModel.tempList } returns emptyList

            logic.deletionNotificationTimedOut()

            verify { repo wasNot Called }
        }
    }


    /**
     * - Verify that [CompositeDisposable.clear] is invoked.
     */
    @Test
    fun onDestroy() {
        logic.onDestroy()

        verify { disposable.clear() }
    }
}
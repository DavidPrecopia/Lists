package com.precopia.david.lists.view.itemlist

import androidx.lifecycle.Observer
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.itemlist.IItemViewContract.LogicEvents
import com.precopia.david.lists.view.itemlist.IItemViewContract.ViewEvents
import com.precopia.domain.datamodel.Item
import com.precopia.domain.datamodel.UserList
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ExtendWith(value = [InstantExecutorExtension::class])
class ItemLogicTest {

    private val viewModel = mockk<IItemViewContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val disposable = spyk<CompositeDisposable>()


    private val logic = ItemLogic(viewModel, repo, schedulerProvider, disposable)


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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val itemList = mutableListOf(itemOne)

            every { repo.getItems(userListId) } returns Flowable.just(itemList)
            every { viewModel.viewData } returns itemList

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart)

            verify { viewModel.viewData = itemList }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetViewData(itemList))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetViewData(itemList))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.SetStateDisplayList)

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { repo.getItems(userListId) } returns Flowable.just(emptyList)
            every { viewModel.viewData } returns emptyList
            every { viewModel.errorMsgEmptyList } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart)

            verify { viewModel.viewData = emptyList }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetStateLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetViewData(emptyList))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.SetStateError(message))

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val throwable = mockk<Throwable>(relaxed = true)

            every { viewModel.viewData } returns emptyList
            every { repo.getItems(userListId) } returns Flowable.error(throwable)
            every { viewModel.errorMsg } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart)

            verify { throwable.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetStateLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetStateError(message))

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val userListList = listOf(UserList(title, position, userListId))
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList
            every { repo.getItems(userListId) } returns Flowable.just(itemList)
            every { repo.userListDeletedObservable } returns Flowable.just(userListList)
            every { viewModel.getMsgListDeleted(title) } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart)

            assertThat(listLiveDataOutput.size).isEqualTo(5)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetViewData(itemList))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.ShowMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)
            assertThat(listLiveDataOutput[3]).isEqualTo(ViewEvents.SetViewData(itemList))
            assertThat(listLiveDataOutput[4]).isEqualTo(ViewEvents.SetStateDisplayList)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - Observe the observer from the Repo.
         * - Check each UserList's ID against the UserListId in the ViewModel.
         * - In this test they will not be equal, thus nothing will happen.
         */
        @Test
        fun `observeDeletedUserLists - UserListId does not match`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val userListIdShouldNotMatch = ""
            val userListList = listOf(UserList(title, position, userListIdShouldNotMatch))
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList
            every { repo.getItems(any()) } returns Flowable.just(itemList)
            every { repo.userListDeletedObservable } returns Flowable.just(userListList)
            every { viewModel.getMsgListDeleted(title) } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart)

            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetViewData(itemList))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetViewData(itemList))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.SetStateDisplayList)

            logic.observe().removeObserver(liveDataObserver)
        }
    }


    @Nested
    inner class Add {
        /**
         * - [IItemViewContract.ViewEvents.OpenAddDialog] is invoked with the current size.
         * of List containing the view data.
         */
        @Test
        fun add() {
            val itemList = mutableListOf(itemOne)
            val size = itemList.size

            every { viewModel.viewData } returns itemList

            logic.onEvent(LogicEvents.Add)

            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.OpenAddDialog(userListId, size))
            }
        }
    }


    @Nested
    inner class Edit {
        /**
         * - Get the Item at the passed-in position.
         * - Pass that Item to [IItemViewContract.ViewEvents.OpenEditDialog].
         */
        @Test
        fun edit() {
            val position = 0
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList

            logic.onEvent(LogicEvents.Edit(position))

            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.OpenEditDialog(itemOne))
            }
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
                logic.onEvent(LogicEvents.Edit(invalidPosition))
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

            logic.onEvent(LogicEvents.Dragging(fromPosition, toPosition, adapter))

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
                logic.onEvent(LogicEvents.Dragging(fromPosition, toPosition, adapter))
            }
        }
    }


    @Nested
    inner class MovePermanently {
        /**
         * - Get the moved Item from the ViewModel.
         * - Invoke [IRepositoryContract.Repository.updateItemPosition].
         *   - This will succeed.
         */
        @Test
        fun `movedPermanently - success`() {
            val newPosition = 0
            val itemList = mutableListOf(itemOne)

            every { viewModel.viewData } returns itemList
            every {
                repo.updateItemPosition(itemOne.id, itemOne.userListId, itemOne.position, newPosition)
            } answers {
                Completable.complete()
            }

            logic.onEvent(LogicEvents.MovedPermanently(newPosition))

            verify { repo.updateItemPosition(itemOne.id, itemOne.userListId, itemOne.position, newPosition) }
        }

        /**
         * - Get the moved Item from the ViewModel.
         * - Invoke [IRepositoryContract.Repository.updateItemPosition].
         *   - This will fail.
         * - Exception is thrown.
         * - Display a message.
         */
        @Test
        fun `movedPermanently - failure`() {
            val throwable = mockk<Throwable>(relaxed = true)
            val newPosition = 0
            val itemList = mutableListOf(itemOne)

            every { viewModel.errorMsg } returns message
            every { viewModel.viewData } returns itemList
            every {
                repo.updateItemPosition(itemOne.id, itemOne.userListId, itemOne.position, newPosition)
            } answers {
                Completable.error(throwable)
            }

            logic.onEvent(LogicEvents.MovedPermanently(newPosition))

            verify { repo.updateItemPosition(itemOne.id, itemOne.userListId, itemOne.position, newPosition) }
            verify { throwable.printStackTrace() }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.ShowMessage(message))
            }
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
                logic.onEvent(LogicEvents.MovedPermanently(invalidPosition))
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

            logic.onEvent(LogicEvents.Delete(position, adapter))

            verify { adapter.remove(position) }
            assertThat(tempItemLists).containsExactly(itemOne)
            verify { viewModel.tempPosition = position }
            assertThat(itemList.isEmpty()).isTrue()
            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.NotifyUserOfDeletion(message))
            }
        }

        /**
         * - Attempt to get the selected Item from the ViewModel with the invalid position.
         * - Exception is thrown
         */
        @Test
        fun `delete - Invalid Position`() {
            val invalidPosition = -1

            logic.onEvent(LogicEvents.Delete(invalidPosition, adapter))

            verify { adapter wasNot Called }
            verify { viewModel wasNot Called }
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
         *    - This will succeed.
         * - Clear the temp List.
         */
        @Test
        fun `undoRecentDeletion - success`() {
            val itemList = mutableListOf<Item>()
            val tempItemList = mutableListOf(itemOne, itemTwo)
            val tempPosition = 0

            every { viewModel.viewData } returns itemList
            every { viewModel.tempList } returns tempItemList
            every { viewModel.tempPosition } returns tempPosition
            every { repo.deleteItems(tempItemList) } answers { Completable.complete() }

            logic.onEvent(LogicEvents.UndoRecentDeletion(adapter))

            verify { adapter.reAdd(tempPosition, itemTwo) }
            assertThat(itemList).containsExactly(itemTwo)
            verify { repo.deleteItems(tempItemList) }
            assertThat(tempItemList.isEmpty()).isTrue()
        }

        /**
         * - Get the last added Item from the ViewModel's temp list.
         * - Re-add to the adapter using the temp position from the ViewModel.
         * - Re-add to the ViewData List.
         * - Remove the last Item from the temp List.
         * - Because the temp List is not empty, pass the temp List to the repo for deletion.
         *   - This will fail.
         * - Exception is thrown.
         * - Display a message.
         * - Clear the temp List.
         */
        @Test
        fun `undoRecentDeletion - failure`() {
            val throwable = mockk<Throwable>(relaxed = true)
            val itemList = mutableListOf<Item>()
            val tempItemList = mutableListOf(itemOne, itemTwo)
            val tempPosition = 0

            every { viewModel.errorMsg } returns message
            every { viewModel.viewData } returns itemList
            every { viewModel.tempList } returns tempItemList
            every { viewModel.tempPosition } returns tempPosition
            every { repo.deleteItems(tempItemList) } answers { Completable.error(throwable) }

            logic.onEvent(LogicEvents.UndoRecentDeletion(adapter))

            verify { adapter.reAdd(tempPosition, itemTwo) }
            assertThat(itemList).containsExactly(itemTwo)
            verify { repo.deleteItems(tempItemList) }
            verify { throwable.printStackTrace() }
            assertThat(tempItemList.isEmpty()).isTrue()
            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.ShowMessage(message))
            }
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
                logic.onEvent(LogicEvents.UndoRecentDeletion(adapter))
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
                logic.onEvent(LogicEvents.UndoRecentDeletion(adapter))
            }
        }
    }


    @Nested
    inner class DeleteNotificationTimeOut {
        /**
         * - Check if the temp List is empty.
         *   - Will not be in this test.
         * - Pass the temp List [IRepositoryContract.Repository.deleteItems].
         *   - This will succeed.
         * - Clear the temp List.
         */
        @Test
        fun `deletionNotificationTimedOut - success`() {
            val itemList = mutableListOf(itemOne)

            every { viewModel.tempList } returns itemList
            every { repo.deleteItems(itemList) } answers { Completable.complete() }

            logic.onEvent(LogicEvents.DeletionNotificationTimedOut)

            verify { repo.deleteItems(itemList) }
            assertThat(itemList.isEmpty()).isTrue()
        }

        /**
         * - Check if the temp List is empty.
         *   - Will not be in this test.
         * - Pass the temp List [IRepositoryContract.Repository.deleteItems].
         *   - This will fail.
         * - Exception is thrown.
         * - Display a message.
         * - Clear the temp List.
         */
        @Test
        fun `deletionNotificationTimedOut - failure`() {
            val throwable = mockk<Throwable>(relaxed = true)
            val itemList = mutableListOf(itemOne)

            every { viewModel.errorMsg } returns message
            every { viewModel.tempList } returns itemList
            every { repo.deleteItems(itemList) } answers { Completable.error(throwable) }

            logic.onEvent(LogicEvents.DeletionNotificationTimedOut)

            verify { repo.deleteItems(itemList) }
            verify { throwable.printStackTrace() }
            assertThat(itemList.isEmpty()).isTrue()
            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.ShowMessage(message))
            }
        }

        /**
         * - Check if the temp List is empty.
         * - Return without invoking any other method.
         */
        @Test
        fun `deletionNotificationTimedOut - Empty Temp List`() {
            every { viewModel.tempList } returns emptyList

            logic.onEvent(LogicEvents.DeletionNotificationTimedOut)

            verify { repo wasNot Called }
        }
    }
}
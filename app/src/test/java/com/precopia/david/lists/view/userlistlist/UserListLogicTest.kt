package com.precopia.david.lists.view.userlistlist

import androidx.lifecycle.Observer
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.userlistlist.IUserListViewContract.LogicEvents
import com.precopia.david.lists.view.userlistlist.IUserListViewContract.ViewEvents
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
class UserListLogicTest {

    private val viewModel = mockk<IUserListViewContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val disposable = spyk<CompositeDisposable>()

    private val logic = UserListLogic(viewModel, repo, schedulerProvider, disposable)


    private val adapter = mockk<IUserListViewContract.Adapter>(relaxUnitFun = true)


    private val id = "id"
    private val title = "title"
    private val position = 0
    private val userListOne = UserList(title, position, id)
    private val userListTwo = UserList(title + 2, position + 1, id + 2)

    private val message = "message"

    private val emptyList = mutableListOf<UserList>()


    @BeforeEach
    fun init() {
        clearAllMocks()
        SchedulerProviderMockInit.init(schedulerProvider)
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
            val userListList = mutableListOf(userListOne)

            every { repo.getUserLists() } returns Flowable.just(userListList)
            every { viewModel.viewData } returns userListList

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart)

            verify { viewModel.viewData = userListList }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetViewData(userListList))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetViewData(userListList))
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

            every { repo.getUserLists() } returns Flowable.just(emptyList)
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
            every { repo.getUserLists() } returns Flowable.error(throwable)
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
    inner class UserListSelected {
        /**
         * - Get the selected UserList with the position argument.
         * - Pass that UserList to the View.
         */
        @Test
        fun `userListSelected - Valid Position`() {
            val position = 0
            val userListList = mutableListOf(userListOne)

            every { viewModel.viewData } returns userListList

            logic.onEvent(LogicEvents.UserListSelected(position))

            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.OpenUserList(userListList[position]))
            }
        }

        /**
         * - Attempt to get the selected UserList from the ViewModel with the invalid position.
         * - Exception is not thrown - it is caught.
         */
        @Test
        fun `userListSelected - Invalid Position`() {
            val invalidPosition = -1
            val userListList = mutableListOf(userListOne)

            every { viewModel.viewData } returns userListList

            logic.onEvent(LogicEvents.UserListSelected(invalidPosition))
        }
    }


    /**
     * - [IUserListViewContract.ViewEvents.OpenAddDialog] is invoked with the current size.
     * of List containing the view data.
     */
    @Test
    fun add() {
        val userListList = mutableListOf(userListOne)
        val size = userListList.size

        every { viewModel.viewData } returns userListList

        logic.onEvent(LogicEvents.Add)

        logic.observe().observeForTesting {
            assertThat(logic.observe().value).isEqualTo(ViewEvents.OpenAddDialog(size))
        }
    }


    @Nested
    inner class Edit {
        /**
         * - Get the UserList at the passed-in position.
         * - Pass that UserList to [IUserListViewContract.ViewEvents.OpenEditDialog].
         */
        @Test
        fun edit() {
            val position = 0
            val userListList = mutableListOf(userListOne)

            every { viewModel.viewData } returns userListList

            logic.onEvent(LogicEvents.Edit(position))

            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.OpenEditDialog(userListOne))
            }
        }

        /**
         * - Attempt to get the selected UserList from the ViewModel with the invalid position.
         * - Exception is thrown.
         */
        @Test
        fun `edit - Invalid Position`() {
            val invalidPosition = -1
            val userListList = mutableListOf(userListOne)

            every { viewModel.viewData } returns userListList

            assertThrows<IndexOutOfBoundsException> {
                logic.onEvent(LogicEvents.Edit(invalidPosition))
            }
        }
    }


    @Nested
    inner class Dragging {
        /**
         * - Updated the Adapter with [IUserListViewContract.Adapter.move].
         * - Update the ViewModel's view data - verify that the order has changed.
         */
        @Test
        fun dragging() {
            val fromPosition = 0
            val toPosition = 1
            val userListList = mutableListOf(userListOne, userListTwo)

            every { viewModel.viewData } returns userListList

            logic.onEvent(LogicEvents.Dragging(fromPosition, toPosition, adapter))

            verify { adapter.move(fromPosition, toPosition) }
            // Assert that the ViewData was updated.
            assertThat(userListList).containsExactly(userListTwo, userListOne)
        }

        /**
         * - Attempt to re-arrange the ViewData with the invalid positions.
         * - Exception is thrown.
         */
        @ParameterizedTest
        @CsvSource("-1, 0", "0, -1", "-1, -1")
        fun `dragging - Invalid positions`(fromPosition: Int, toPosition: Int) {
            val userListList = mutableListOf(userListOne, userListTwo)

            every { viewModel.viewData } returns userListList

            assertThrows<IndexOutOfBoundsException> {
                logic.onEvent(LogicEvents.Dragging(fromPosition, toPosition, adapter))
            }
        }
    }


    @Nested
    inner class MovePermanently {
        /**
         * - Get the moved UserList from the ViewModel.
         * - Invoke [IRepositoryContract.Repository.updateUserListPosition].
         *   - This will succeed.
         */
        @Test
        fun `movedPermanently - success`() {
            val newPosition = 0
            val userListList = mutableListOf(userListOne)

            every { viewModel.viewData } returns userListList
            every {
                repo.updateUserListPosition(userListOne.id, userListOne.position, newPosition)
            } answers {
                Completable.complete()
            }

            logic.onEvent(LogicEvents.MovedPermanently(newPosition))

            verify { repo.updateUserListPosition(userListOne.id, userListOne.position, newPosition) }
        }

        /**
         * - Get the moved UserList from the ViewModel.
         * - Invoke [IRepositoryContract.Repository.updateUserListPosition].
         *   - This will fail.
         * - Exception is thrown.
         */
        @Test
        fun `movedPermanently - error`() {
            val throwable = mockk<Throwable>(relaxed = true)
            val newPosition = 0
            val userListList = mutableListOf(userListOne)

            every { viewModel.viewData } returns userListList
            every {
                repo.updateUserListPosition(userListOne.id, userListOne.position, newPosition)
            } answers {
                Completable.error(throwable)
            }

            logic.onEvent(LogicEvents.MovedPermanently(newPosition))

            verify { repo.updateUserListPosition(userListOne.id, userListOne.position, newPosition) }
            verify { throwable.printStackTrace() }
        }

        /**
         * - Attempt to get the selected UserList from the ViewModel with the invalid position.
         * - Exception is thrown.
         */
        @Test
        fun `movedPermanently - InvalidPosition`() {
            val invalidPosition = -1
            val userListList = mutableListOf(userListOne)

            every { viewModel.viewData } returns userListList

            assertThrows<IndexOutOfBoundsException> {
                logic.onEvent(LogicEvents.MovedPermanently(invalidPosition))
            }
        }
    }


    @Nested
    inner class Delete {
        /**
         * - Get the deleted UserList from the ViewModel.
         * - Remove from the Adapter.
         * - Save the deleted UserList itself to the ViewModel's temp list.
         * - Save the deleted UserList's position to the ViewModel.
         * - Remove from the view data in the ViewModel.
         * - Notify user of deletion.
         */
        @Test
        fun delete() {
            val position = 0
            val userListList = mutableListOf(userListOne)
            val tempUserLists = mutableListOf<UserList>()

            every { viewModel.viewData } returns userListList
            every { viewModel.tempList } returns tempUserLists
            every { viewModel.msgDeletion } returns message

            logic.onEvent(LogicEvents.Delete(position, adapter))

            verify { adapter.remove(position) }
            assertThat(tempUserLists).containsExactly(userListOne)
            verify { viewModel.tempPosition = position }
            assertThat(userListList.isEmpty()).isTrue()
            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.NotifyUserOfDeletion(message))
            }
        }

        /**
         * - Attempt to get the selected UserList from the ViewModel with the invalid position.
         * - Nothing happens.
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
         * - Get the last added UserList from the ViewModel's temp list.
         * - Re-add to the adapter using the temp position from the ViewModel.
         * - Re-add to the ViewData List.
         * - Remove the last UserList from the temp List.
         * - Pass the temp List to [IRepositoryContract.Repository.deleteUserLists].
         *   - This will succeed.
         * - Clear the temp List.
         */
        @Test
        fun `undoRecentDeletion - success`() {
            val userListList = mutableListOf<UserList>()
            val tempUserLists = mutableListOf(userListOne, userListTwo)
            val tempPosition = 0

            every { viewModel.viewData } returns userListList
            every { viewModel.tempList } returns tempUserLists
            every { viewModel.tempPosition } returns tempPosition
            every { repo.deleteUserLists(tempUserLists) } answers { Completable.complete() }

            logic.onEvent(LogicEvents.UndoRecentDeletion(adapter))

            verify { adapter.reAdd(tempPosition, userListTwo) }
            assertThat(userListList).containsExactly(userListTwo)
            verify { repo.deleteUserLists(tempUserLists) }
            assertThat(tempUserLists.isEmpty()).isTrue()
        }

        /**
         * - Get the last added UserList from the ViewModel's temp list.
         * - Re-add to the adapter using the temp position from the ViewModel.
         * - Re-add to the ViewData List.
         * - Remove the last UserList from the temp List.
         * - Pass the temp List to [IRepositoryContract.Repository.deleteUserLists].
         *   - This will fail.
         * - Throw the Exception.
         * - Clear the temp list.
         * - Display error message.
         */
        @Test
        fun `undoRecentDeletion - error`() {
            val throwable = mockk<Throwable>(relaxed = true)
            val userListList = mutableListOf<UserList>()
            val tempUserLists = mutableListOf(userListOne, userListTwo)
            val tempPosition = 0

            every { viewModel.errorMsg } returns message
            every { viewModel.viewData } returns userListList
            every { viewModel.tempList } returns tempUserLists
            every { viewModel.tempPosition } returns tempPosition
            every { repo.deleteUserLists(tempUserLists) } answers { Completable.error(throwable) }

            logic.onEvent(LogicEvents.UndoRecentDeletion(adapter))

            verify { adapter.reAdd(tempPosition, userListTwo) }
            assertThat(userListList).containsExactly(userListTwo)
            verify { repo.deleteUserLists(tempUserLists) }
            verify { throwable.printStackTrace() }
            assertThat(tempUserLists.isEmpty()).isTrue()
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
            val tempUserLists = mutableListOf(userListOne)

            every { viewModel.tempList } returns tempUserLists
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
         * - Pass the temp List to [IRepositoryContract.Repository.deleteUserLists].
         *   - This will succeed.
         * - Clear the temp List.
         */
        @Test
        fun `deletionNotificationTimedOut - success`() {
            val userListList = mutableListOf(userListOne)

            every { viewModel.tempList } returns userListList
            every { repo.deleteUserLists(userListList) } answers { Completable.complete() }

            logic.onEvent(LogicEvents.DeletionNotificationTimedOut)

            verify { repo.deleteUserLists(userListList) }
            assertThat(userListList.isEmpty()).isTrue()
        }

        /**
         * - Check if the temp List is empty.
         *   - Will not be in this test.
         * - Pass the temp List to [IRepositoryContract.Repository.deleteUserLists].
         *   - This will fail.
         * - Throw the Exception.
         * - Clear the temp list.
         * - Display error message.
         */
        @Test
        fun `deletionNotificationTimedOut - error`() {
            val throwable = mockk<Throwable>(relaxed = true)
            val userListList = mutableListOf(userListOne)

            every { viewModel.errorMsg } returns message
            every { viewModel.tempList } returns userListList
            every { repo.deleteUserLists(userListList) } answers { Completable.error(throwable) }

            logic.onEvent(LogicEvents.DeletionNotificationTimedOut)

            verify { repo.deleteUserLists(userListList) }
            verify { throwable.printStackTrace() }
            assertThat(userListList.isEmpty()).isTrue()
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


    /**
     * - Invokes [IUserListViewContract.ViewEvents.OpenPreferences]
     */
    @Test
    fun preferencesSelected() {
        logic.onEvent(LogicEvents.PreferencesSelected)

        logic.observe().observeForTesting {
            assertThat(logic.observe().value).isEqualTo(ViewEvents.OpenPreferences)
        }
    }
}
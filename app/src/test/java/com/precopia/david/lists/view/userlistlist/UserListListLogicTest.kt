package com.precopia.david.lists.view.userlistlist

import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.IUtilNightModeContract
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UserListListLogicTest {

    private val view = mockk<IUserListViewContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IUserListViewContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val disposable = spyk<CompositeDisposable>()

    private val utilNightMode = mockk<IUtilNightModeContract>(relaxUnitFun = true)

    private val logic = UserListListLogic(view, viewModel, utilNightMode, repo, schedulerProvider, disposable)


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
            val userListList = mutableListOf(userListOne)

            every { repo.getUserLists() } returns Flowable.just(userListList)
            every { viewModel.viewData } returns userListList

            logic.onStart()

            verify(exactly = 2) { view.setViewData(userListList) }
            verify { viewModel.viewData = userListList }
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
            every { repo.getUserLists() } returns Flowable.just(emptyList)
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
            val throwable = mockk<Throwable>(relaxed = true)

            every { viewModel.viewData } returns emptyList
            every { repo.getUserLists() } returns Flowable.error(throwable)
            every { viewModel.errorMsg } returns message

            logic.onStart()

            verify { view.setStateLoading() }
            verify { throwable.printStackTrace() }
            verify { view.setStateError(message) }
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

            logic.userListSelected(position)

            verify { view.openUserList(userListList[position]) }
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

            logic.userListSelected(invalidPosition)
        }
    }


    /**
     * - [IUserListViewContract.View.openAddDialog] is invoked with the current size.
     * of List containing the view data.
     */
    @Test
    fun add() {
        val userListList = mutableListOf(userListOne)
        val size = userListList.size

        every { viewModel.viewData } returns userListList

        logic.add()

        verify { view.openAddDialog(size) }
    }


    @Nested
    inner class Edit {
        /**
         * - Get the UserList at the passed-in position.
         * - Pass that UserList to [IUserListViewContract.View.openEditDialog].
         */
        @Test
        fun edit() {
            val position = 0
            val userListList = mutableListOf(userListOne)

            every { viewModel.viewData } returns userListList

            logic.edit(position)

            verify { view.openEditDialog(userListOne) }
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
                logic.edit(invalidPosition)
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

            logic.dragging(fromPosition, toPosition, adapter)

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
                logic.dragging(fromPosition, toPosition, adapter)
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

            logic.movedPermanently(newPosition)

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

            logic.movedPermanently(newPosition)

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
                logic.movedPermanently(invalidPosition)
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

            logic.delete(position, adapter)

            verify { adapter.remove(position) }
            assertThat(tempUserLists).containsExactly(userListOne)
            verify { viewModel.tempPosition = position }
            assertThat(userListList.isEmpty()).isTrue()
            verify { view.notifyUserOfDeletion(message) }
        }

        /**
         * - Attempt to get the selected UserList from the ViewModel with the invalid position.
         * - Exception is thrown
         */
        @Test
        fun `delete - Invalid Position`() {
            val invalidPosition = -1
            val userListList = mutableListOf(userListOne)
            val tempUserLists = mutableListOf<UserList>()

            every { viewModel.viewData } returns userListList
            every { viewModel.tempList } returns tempUserLists

            assertThrows<java.lang.IndexOutOfBoundsException> {
                logic.delete(invalidPosition, adapter)
            }
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

            logic.undoRecentDeletion(adapter)

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

            logic.undoRecentDeletion(adapter)

            verify { adapter.reAdd(tempPosition, userListTwo) }
            assertThat(userListList).containsExactly(userListTwo)
            verify { repo.deleteUserLists(tempUserLists) }
            verify { throwable.printStackTrace() }
            assertThat(tempUserLists.isEmpty()).isTrue()
            verify { view.showMessage(message) }
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
            val tempUserLists = mutableListOf(userListOne)

            every { viewModel.tempList } returns tempUserLists
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
         * - Pass the temp List to [IRepositoryContract.Repository.deleteUserLists].
         *   - This will succeed.
         * - Clear the temp List.
         */
        @Test
        fun `deletionNotificationTimedOut - success`() {
            val userListList = mutableListOf(userListOne)

            every { viewModel.tempList } returns userListList
            every { repo.deleteUserLists(userListList) } answers { Completable.complete() }

            logic.deletionNotificationTimedOut()

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

            logic.deletionNotificationTimedOut()

            verify { repo.deleteUserLists(userListList) }
            verify { throwable.printStackTrace() }
            assertThat(userListList.isEmpty()).isTrue()
            verify { view.showMessage(message) }
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
     * - Invokes [IUserListViewContract.View.openPreferences]
     */
    @Test
    fun preferencesSelected() {
        logic.preferencesSelected()

        verify { view.openPreferences() }
    }


    @Nested
    inner class NightMode {
        /**
         * - Menu item is unchecked.
         * - Night mode is enabled.
         */
        @Test
        fun `setNightMode - Unchecked`() {
            logic.setNightMode(false)

            verify { utilNightMode.setNight() }
        }

        /**
         * - Menu item is Checked.
         * - Night mode is Disabled.
         */
        @Test
        fun `setNightMode - Checked`() {
            logic.setNightMode(true)

            verify { utilNightMode.setDay() }
        }

        /**
         * - True is returned when night mode is enabled.
         */
        @Test
        fun isNightModeEnabled() {
            val nightModeEnabled = true

            every { utilNightMode.nightModeEnabled } returns nightModeEnabled

            assertThat(logic.isNightModeEnabled).isEqualTo(nightModeEnabled)
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
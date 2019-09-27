package com.example.david.lists.view.userlistlist

import com.example.david.lists.SchedulerProviderMockInit
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.repository.IRepositoryContract.Repository
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.IUtilNightModeContract
import com.example.david.lists.view.authentication.IAuthContract
import com.example.david.lists.view.authentication.IAuthContract.AuthResult
import com.example.david.lists.view.userlistlist.IUserListViewContract.*
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
class UserListListLogicTest {

    @Mock
    private lateinit var view: View

    @Mock
    private lateinit var viewModel: ViewModel

    @Mock
    private lateinit var repo: Repository

    @Mock
    private lateinit var schedulerProvider: ISchedulerProviderContract

    @Spy
    private lateinit var disposable: CompositeDisposable

    @Mock
    private lateinit var utilNightMode: IUtilNightModeContract


    @Mock
    private lateinit var adapter: Adapter


    @InjectMocks
    private lateinit var logic: UserListListLogic


    private val id = "id"
    private val title = "title"
    private val position = 0

    private val userList = UserList(title, position, id)
    private val userListTwo = UserList(title + 2, position + 1, id + 2)


    private val userListList = mutableListOf(userList)

    private val invalidPosition = -1
    private val errorMsg = "error"


    @Before
    fun setUp() {
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    /**
     * Normal behavior
     * - Set the View's data because the ViewModel's view data is not empty.
     * - Get List from repo.
     * - Save List to ViewModel.
     * - Set the View's data
     * - Set View state display list.
     */
    @Test
    fun onStart() {
        `when`(repo.getUserLists).thenReturn(Flowable.just(userListList))
        `when`(viewModel.viewData).thenReturn(userListList)

        logic.onStart()

        verify(viewModel).viewData = userListList
        verify(view, times(2)).setViewData(userListList)
        verify(view).setStateDisplayList()
    }

    /**
     * Error behavior - empty List returned by repo,
     * - Set View state loading.
     * - Get List from repo.
     * - Save List to ViewModel.
     * - Submit the List to the View.
     * - Set View state error with message from the ViewModel.
     */
    @Test
    fun onStartEmptyList() {
        val emptyList = mutableListOf<UserList>()

        `when`(repo.getUserLists).thenReturn(Flowable.just(emptyList))
        `when`(viewModel.viewData).thenReturn(emptyList)
        `when`(viewModel.errorMsgEmptyList).thenReturn(errorMsg)

        logic.onStart()

        verify(view).setStateLoading()
        verify(viewModel).viewData = emptyList
        verify(view).setViewData(emptyList)
        verify(view).setStateError(errorMsg)
    }

    /**
     * Error behavior - the repo throws an error,
     * - Set View state loading.
     * - Get List from repo.
     * - Repo throws an error.
     * - Set View state error with message from ViewModel.
     */
    @Test
    fun onStartRepoThrowsError() {
        val throwable = Throwable()

        `when`(repo.getUserLists).thenReturn(Flowable.error(throwable))
        `when`(viewModel.errorMsg).thenReturn(errorMsg)

        logic.onStart()

        verify(view).setStateLoading()
        verify(view).setStateError(errorMsg)
    }


    /**
     * Normal behavior - valid position,
     * - Get the selected UserList with the position argument.
     * - Pass that UserList to [View.openUserList]
     */
    @Test
    fun userListSelectedValidPosition() {
        `when`(viewModel.viewData).thenReturn(userListList)

        logic.userListSelected(position)

        verify(view).openUserList(userList)
    }

    /**
     * Error behavior - invalid position,
     * - Throws an exception.
     */
    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun userListSelectedInvalidPosition() {
        `when`(viewModel.viewData).thenReturn(userListList)

        logic.userListSelected(invalidPosition)
    }


    /**
     * Normal behavior
     * - [View.openAddDialog] is invoked with the current size
     * of List containing the view data.
     */
    @Test
    fun add() {
        val size = userListList.size

        `when`(viewModel.viewData).thenReturn(userListList)

        logic.add()

        verify(view).openAddDialog(size)
    }


    /**
     * Normal behavior
     * - Get the UserList at the passed-in position.
     * - Invoked [View.openEditDialog].
     */
    @Test
    fun edit() {
        val editPosition = 0
        val editUserLists = mutableListOf<UserList>()
        editUserLists.add(userList)

        `when`(viewModel.viewData).thenReturn(editUserLists)

        logic.edit(editPosition)

        verify(view).openEditDialog(userList)
    }

    /**
     * Error behavior - invalid position,
     * - Exception is thrown.
     */
    @Test(expected = IllegalArgumentException::class)
    fun editInvalidPosition() {
        logic.edit(invalidPosition)
    }


    /**
     * Normal behavior
     * - Updated the Adapter with [Adapter.move]
     * - Update the ViewModel's view data.
     */
    @Test
    fun dragging() {
        val fromPos = 0
        val toPos = 1

        val userListsDragging = mutableListOf(userList, userListTwo)

        `when`(viewModel.viewData).thenReturn(userListsDragging)

        logic.dragging(fromPos, toPos, adapter)

        verify(adapter).move(fromPos, toPos)
        assertThat<List<UserList>>(userListsDragging, `is`(contains(userListTwo, userList)))
    }


    /**
     * Normal behavior
     * - Get the moved UserList from the ViewModel.
     * - Invoke [Repository.updateUserListPosition].
     */
    @Test
    fun movedPermanently() {
        val newPosition = 0
        val moveUserLists = mutableListOf(userList)

        `when`(viewModel.viewData).thenReturn(moveUserLists)

        logic.movedPermanently(newPosition)

        verify(repo).updateUserListPosition(userList, userList.position, position)
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
     * Normal behavior
     * - Remove from the Adapter.
     * - Save the deleted UserList itself to the ViewModel's temp list.
     * - Save the deleted UserList's position to the ViewModel.
     * - Remove from the view data in the ViewModel.
     * - Notify user of deletion.
     */
    @Test
    fun delete() {
        val tempUserLists = mutableListOf<UserList>()

        `when`(viewModel.viewData).thenReturn(userListList)
        `when`(viewModel.tempList).thenReturn(tempUserLists)
        `when`(viewModel.msgDeletion).thenReturn(errorMsg)

        logic.delete(position, adapter)

        assertThat<List<UserList>>(tempUserLists, `is`(contains(userList)))
        assertThat(userListList.isEmpty(), `is`(true))
        verify(adapter).remove(position)
        verify(viewModel).tempPosition = position
        verify(view).notifyUserOfDeletion(errorMsg)
    }

    /**
     * Error behavior - invalid position,
     * - Exception is thrown
     */
    @Test(expected = IllegalArgumentException::class)
    fun deleteInvalidPosition() {
        logic.delete(invalidPosition, adapter)
    }


    /**
     * Normal behavior
     * - Re-add to the adapter.
     * - Re-add to the view data List.
     * - Remove the last UserList from the temp List.
     * - Because the temp List is not empty, pass the temp List to the repo for deletion.
     * - Clear the temp List.
     */
    @Test
    fun undoRecentDeletion() {
        val tempPosition = 0

        val tempList = mutableListOf(userList, userListTwo)
        val viewDataList = mutableListOf<UserList>()

        `when`(viewModel.tempList).thenReturn(tempList)
        `when`(viewModel.tempPosition).thenReturn(tempPosition)
        `when`(viewModel.viewData).thenReturn(viewDataList)

        logic.undoRecentDeletion(adapter)

        verify(adapter).reAdd(tempPosition, userListTwo)
        verify(repo).deleteUserLists(tempList)
        assertThat<List<UserList>>(viewDataList, `is`(contains(userListTwo)))
        assertThat(tempList.isEmpty(), `is`(true))
    }

    /**
     * Error behavior - empty temp List,
     * - An exception is thrown
     */
    @Test(expected = UnsupportedOperationException::class)
    fun undoRecentDeletionEmptyList() {
        `when`(viewModel.tempList).thenReturn(ArrayList())

        logic.undoRecentDeletion(adapter)
    }

    /**
     * Error behavior - invalid position,
     * - An exception is thrown
     */
    @Test(expected = UnsupportedOperationException::class)
    fun undoRecentDeletionInvalidPosition() {
        `when`(viewModel.tempList).thenReturn(userListList)
        `when`(viewModel.tempPosition).thenReturn(invalidPosition)

        logic.undoRecentDeletion(adapter)
    }


    /**
     * Normal behavior
     * - Check if the temp List is empty.
     * - Pass the temp List [Repository.deleteUserLists].
     * - Clear the temp List.
     */
    @Test
    fun deletionNotificationTimedOut() {
        `when`(viewModel.tempList).thenReturn(userListList)

        logic.deletionNotificationTimedOut()

        verify(repo).deleteUserLists(userListList)
        assertThat(userListList.isEmpty(), `is`(true))
    }

    /**
     * Error behavior - empty temp List,
     * - Check if the temp List is empty.
     * - Return without invoking any other method.
     */
    @Test
    fun deletionNotificationTimedOutEmptyTempList() {
        `when`(viewModel.tempList).thenReturn(ArrayList())

        logic.deletionNotificationTimedOut()

        verify(repo, never()).deleteUserLists(anyList())
    }


    /**
     * Normal behavior
     * - Invokes [View.confirmSignOut]
     */
    @Test
    fun signOut() {
        logic.signOut()

        verify(view).confirmSignOut()
    }


    /**
     * Normal behavior
     * - Invoke [View.openAuthentication]
     */
    @Test
    fun signOutConfirmed() {
        val authGoal = IAuthContract.AuthGoal.SIGN_OUT
        val requestCode = 100
        val intentExtraKey = "key"

        `when`(viewModel.requestCode).thenReturn(requestCode)
        `when`(viewModel.intentExtraAuthResultKey).thenReturn(intentExtraKey)

        logic.signOutConfirmed()

        verify(view).openAuthentication(authGoal, requestCode, intentExtraKey)
    }


    /**
     * Normal behavior
     * - [AuthResult.AUTH_SUCCESS] parameter
     * - Invoke [View.recreateView]
     */
    @Test
    fun authResult() {
        logic.authResult(AuthResult.AUTH_SUCCESS)

        verify(view).recreateView()
    }

    /**
     * Error behavior
     * - [AuthResult.AUTH_FAILED] parameter
     * - Nothing invoked.
     */
    @Test
    fun authResultFailed() {
        logic.authResult(AuthResult.AUTH_FAILED)

        verify(view, never()).recreateView()
    }

    /**
     * Error behavior
     * - [AuthResult.AUTH_CANCELLED] parameter
     * - Nothing invoked.
     */
    @Test
    fun authResultCancelled() {
        logic.authResult(AuthResult.AUTH_CANCELLED)

        verify(view, never()).recreateView()
    }


    /**
     * Normal behavior
     * - If the menu item is unchecked, Night mode should be enabled.
     */
    @Test
    fun setNightModeUnchecked() {
        logic.setNightMode(false)

        verify(utilNightMode).setNight()
    }

    /**
     * Normal behavior
     * - If the menu item is checked, Day mode should be enabled.
     */
    @Test
    fun setNightModeChecked() {
        logic.setNightMode(true)

        verify(utilNightMode).setDay()
    }

    /**
     * Normal behavior
     * - Verify true is returned when night mode is enabled.
     */
    @Test
    fun isNightModeEnabled() {
        val nightModeState = true

        `when`(utilNightMode.nightModeEnabled).thenReturn(nightModeState)

        assertThat(
                logic.isNightModeEnabled,
                `is`(nightModeState)
        )
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
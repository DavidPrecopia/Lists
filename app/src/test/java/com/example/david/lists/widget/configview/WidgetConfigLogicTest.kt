package com.example.david.lists.widget.configview

import com.example.david.lists.SchedulerProviderMockInit
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.ISchedulerProviderContract
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class WidgetConfigLogicTest {

    @Mock
    private lateinit var view: IWidgetConfigContract.View
    
    @Mock
    private lateinit var viewModel: IWidgetConfigContract.ViewModel

    @Mock
    private lateinit var repo: IRepositoryContract.Repository

    @Mock
    private lateinit var schedulerProvider: ISchedulerProviderContract

    @Spy
    private lateinit var disposable: CompositeDisposable

    private lateinit var logic: WidgetConfigLogic


    private val position = 0
    private val title = "title"
    private val id = "qwerty"
    private val userList = UserList(title, position, id)

    private val widgetIdValid = 100

    private val resultCanceled = 0


    @Before
    fun setUp() {
        SchedulerProviderMockInit.init(schedulerProvider)
        logic = WidgetConfigLogic(
                view, viewModel, widgetIdValid, repo, schedulerProvider, disposable
        )
    }


    /**
     * Expected, normal behavior - when a valid widget ID is passed-in,
     * - Set the View's state to loading.
     * - Get the List of UserLists from the repo.
     * - Save the List to the ViewModel.
     * - Set the View's data
     * - Set the View's state to display list.
     */
    @Test
    fun onStartWithValidWidgetId() {
        val userLists = listOf(userList)

        `when`(viewModel.viewData).thenReturn(userLists)
        `when`(repo.getUserLists).thenReturn(Flowable.just(userLists))

        logic.onStart()

        verify(view).setResults(widgetIdValid, resultCanceled)
        verify(view).setStateLoading()
        verify(viewModel).viewData = userLists
        verify(view).setViewData(userLists)
        verify(view).setStateDisplayList()
    }

    /**
     * Error behavior - when an in-valid widget ID is passed-in,
     * - Save the widget ID to the ViewModel.
     * - Set the View's result to cancelled.
     * - Finish the View.
     */
    @Test
    fun onStartWithInvalidWidgetId() {
        val widgetIdInvalid = -1
        `when`(viewModel.invalidWidgetId).thenReturn(widgetIdInvalid)
        `when`(viewModel.resultCancelled).thenReturn(resultCanceled)

        // Instantiate with an invalid widget ID.
        WidgetConfigLogic(
                view, viewModel, widgetIdInvalid, repo, schedulerProvider, disposable
        )

        verify(viewModel).widgetId = widgetIdInvalid
        verify(view).setResults(widgetIdInvalid, resultCanceled)
        verify(view).finishViewInvalidId()
    }

    /**
     * Error behavior - when the repo returns an empty List,
     * - Set the View's state to loading.
     * - Get the List of UserLists from the repo.
     * - Save the List to the ViewModel.
     * - Set the View's data
     * - Set the View's state to display error with message from ViewModel.
     */
    @Test
    fun onStartEmptyList() {
        val userLists = ArrayList<UserList>()
        val emptyListError = "error"

        `when`(viewModel.viewData).thenReturn(userLists)
        `when`(viewModel.errorMsgEmptyList).thenReturn(emptyListError)
        `when`(repo.getUserLists).thenReturn(Flowable.just(userLists))

        logic.onStart()

        verify(viewModel).widgetId = widgetIdValid
        verify(view).setResults(widgetIdValid, resultCanceled)
        verify(view).setStateLoading()
        verify(viewModel).viewData = userLists
        verify(view).setViewData(userLists)
        verify(view).setStateError(emptyListError)
    }

    /**
     * Error behavior - when the repo throws an error,
     * - Set the View's state to loading.
     * - Get the List of UserLists from the repo.
     * - Repo thrown an error
     * - Set the View's state to display error with message from ViewModel.
     */
    @Test
    fun onStartRepoThrowsError() {
        val throwable = Throwable()
        val errorMsg = "error"

        `when`(viewModel.errorMsg).thenReturn(errorMsg)
        `when`(repo.getUserLists).thenReturn(Flowable.error(throwable))

        logic.onStart()

        verify(viewModel).widgetId = widgetIdValid
        verify(view).setResults(widgetIdValid, resultCanceled)
        verify(view).setStateLoading()
        verify(view).setStateError(errorMsg)
    }


    /**
     * Normal behavior.
     * - Save the details of the selected UserList via a View method.
     * - Set the View's result to successful.
     * - Finish the View.
     */
    @Test
    fun selectedUserList() {
        val resultOk = 1
        val sharedPrefId = "id"
        val sharedPrefTitle = "title"

        val userLists = listOf(userList)

        `when`(viewModel.viewData).thenReturn(userLists)
        `when`(viewModel.widgetId).thenReturn(widgetIdValid)
        `when`(viewModel.resultOk).thenReturn(resultOk)
        `when`(viewModel.sharedPrefKeyId).thenReturn(sharedPrefId)
        `when`(viewModel.sharedPrefKeyTitle).thenReturn(sharedPrefTitle)

        logic.selectedUserList(position)

        verify(view).saveDetails(userList.id, userList.title, sharedPrefId, sharedPrefTitle)
        verify(view).setResults(widgetIdValid, resultOk)
        verify(view).finishView(widgetIdValid)
    }


    /**
     * Normal behavior.
     * Verify the CompositeDisposable is cleared.
     */
    @Test
    fun onDestroy() {
        logic.onDestroy()
        verify(disposable, atMostOnce()).clear()
    }
}

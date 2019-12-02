package com.example.david.lists.widget.configview

import com.example.david.lists.SchedulerProviderMockInit
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.domain.datamodel.UserList
import com.example.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class WidgetConfigLogicTest {

    private val view = mockk<IWidgetConfigContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IWidgetConfigContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val disposable = spyk<CompositeDisposable>()

    private val logic = WidgetConfigLogic(view, viewModel, repo, schedulerProvider, disposable)


    private val position = 0
    private val title = "title"
    private val id = "qwerty"
    private val userList = UserList(title, position, id)

    private val widgetIdValid = 100
    private val widgetIdInvalid = -1
    private val resultCanceled = 0


    @BeforeEach
    fun init() {
        clearAllMocks()
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    @Nested
    inner class OnStart {
        /**
         * - Set the View's state to loading.
         * - Save the Widget's ID to the ViewModel.
         * - Set the View's result to cancelled.
         * - Check if Widget ID argument equals the invalid ID from ViewModel - will not equal in this test.
         * - Get the List of UserLists from the repo.
         * - Save the List to the ViewModel.
         * - Set the View's data
         * - Set the View's state to display list.
         */
        @Test
        fun `onStart - Valid WidgetId`() {
            val userListList = listOf(userList)

            every { viewModel.invalidWidgetId } returns widgetIdInvalid
            every { viewModel.resultCancelled } returns resultCanceled
            every { viewModel.viewData } returns userListList
            every { repo.getUserLists } returns Flowable.just(userListList)

            logic.onStart(widgetIdValid)

            verify { view.setStateLoading() }
            verify { viewModel.widgetId = widgetIdValid }
            verify { view.setResults(widgetIdValid, resultCanceled) }
            verify { repo.getUserLists }
            verify { viewModel.viewData = userListList }
            verify { view.setViewData(userListList) }
            verify { view.setStateDisplayList() }
        }

        /**
         * - Set the View's state to loading.
         * - Save the widget ID to the ViewModel.
         * - Set the View's result to cancelled.
         * - Check if Widget ID argument equals the invalid ID from ViewModel - will equal true in this test.
         * - Finish the View.
         */
        @Test
        fun `onStart - Invalid WidgetId`() {
            every { viewModel.invalidWidgetId } returns widgetIdInvalid
            every { viewModel.resultCancelled } returns resultCanceled

            logic.onStart(widgetIdInvalid)

            verify { view.setStateLoading() }
            verify { viewModel.widgetId = widgetIdInvalid }
            verify { view.setResults(widgetIdInvalid, resultCanceled) }
            verify { view.finishViewInvalidId() }
        }

        /**
         * - Set the View's state to loading.
         * - Save the Widget's ID to the ViewModel.
         * - Set the View's result to cancelled.
         * - Check if Widget ID argument equals the invalid ID from ViewModel - will not equal in this test.
         * - Get the List of UserLists from the repo.
         * - Save the List to the ViewModel.
         * - Set the View's data
         * - Set the View's state to display error with message from ViewModel.
         */
        @Test
        fun `onStart - Repo returns an empty List`() {
            val emptyList = emptyList<UserList>()
            val emptyListError = "error"

            every { viewModel.invalidWidgetId } returns widgetIdInvalid
            every { viewModel.resultCancelled } returns resultCanceled
            every { viewModel.viewData } returns emptyList
            every { viewModel.errorMsgEmptyList } returns emptyListError
            every { repo.getUserLists } returns Flowable.just(emptyList)

            logic.onStart(widgetIdValid)

            verify { view.setStateLoading() }
            verify { viewModel.widgetId = widgetIdValid }
            verify { view.setResults(widgetIdValid, resultCanceled) }
            verify { viewModel.viewData = emptyList }
            verify { view.setViewData(emptyList) }
            verify { view.setStateError(emptyListError) }
        }

        /**
         * - Set the View's state to loading.
         * - Save the Widget's ID to the ViewModel.
         * - Set the View's result to cancelled.
         * - Check if Widget ID argument equals the invalid ID from ViewModel - will not equal in this test.
         * - Get the List of UserLists from the repo.
         * - Repo thrown an error
         * - Set the View's state to display error with message from ViewModel.
         */
        @Test
        fun `onStart - Repo returns an error`() {
            val throwable = Throwable()
            val errorMsg = "error"

            every { viewModel.invalidWidgetId } returns widgetIdInvalid
            every { viewModel.resultCancelled } returns resultCanceled
            every { viewModel.errorMsg } returns errorMsg
            every { repo.getUserLists } returns Flowable.error(throwable)

            logic.onStart(widgetIdValid)

            verify { view.setStateLoading() }
            verify { viewModel.widgetId = widgetIdValid }
            verify { view.setResults(widgetIdValid, resultCanceled) }
            verify { view.setStateError(errorMsg) }
        }
    }


    @Nested
    inner class SelectedUserList {
        /**
         * - Get the selected UserList from the ViewModel
         * - Save the details of the selected UserList via a View method.
         * - Set the View's result to successful.
         * - Finish the View.
         */
        @Test
        fun selectedUserList() {
            val resultOk = 1
            val sharedPrefId = "id"
            val sharedPrefTitle = "title"

            val userListList = listOf(userList)

            every { viewModel.viewData } returns userListList
            every { viewModel.widgetId } returns widgetIdValid
            every { viewModel.resultOk } returns resultOk
            every { viewModel.sharedPrefKeyId } returns sharedPrefId
            every { viewModel.sharedPrefKeyTitle } returns sharedPrefTitle

            logic.selectedUserList(position)

            verify { view.saveDetails(userList.id, userList.title, sharedPrefId, sharedPrefTitle) }
            verify { view.setResults(widgetIdValid, resultOk) }
            verify { view.finishView(widgetIdValid) }
        }

        /**
         * - Get the selected UserList from the ViewModel with an negative position - Exception is thrown.
         */
        @Test
        fun `selectedUserList - Negative position argument`() {
            val userListList = listOf(userList)
            val negativePosition = -1

            every { viewModel.viewData } returns userListList

            assertThrows<IndexOutOfBoundsException> {
                logic.selectedUserList(negativePosition)
            }
        }
    }


    /**
     * Verify that the CompositeDisposable is cleared.
     */
    @Test
    fun onDestroy() {
        logic.onDestroy()

        verify { disposable.clear() }
    }
}

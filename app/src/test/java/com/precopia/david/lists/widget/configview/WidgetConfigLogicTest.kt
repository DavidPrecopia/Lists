package com.precopia.david.lists.widget.configview

import androidx.lifecycle.Observer
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.widget.configview.IWidgetConfigContract.LogicEvents
import com.precopia.david.lists.widget.configview.IWidgetConfigContract.ViewEvents
import com.precopia.domain.datamodel.UserList
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [InstantExecutorExtension::class])
class WidgetConfigLogicTest {

    private val viewModel = mockk<IWidgetConfigContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val disposable = spyk<CompositeDisposable>()


    private val logic = WidgetConfigLogic(
            viewModel, repo, schedulerProvider, disposable
    )


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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.invalidWidgetId } returns widgetIdInvalid
            every { viewModel.resultCancelled } returns resultCanceled
            every { viewModel.viewData } returns userListList
            every { repo.getUserLists() } returns Flowable.just(userListList)

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(widgetIdValid))

            verify { viewModel.widgetId = widgetIdValid }
            verify { repo.getUserLists() }
            verify { viewModel.viewData = userListList }
            assertThat(listLiveDataOutput.size).isEqualTo(4)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetStateLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetResults(widgetIdValid, resultCanceled))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.SetViewData(userListList))
            assertThat(listLiveDataOutput[3]).isEqualTo(ViewEvents.SetStateDisplayList)

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(widgetIdInvalid))

            verify { viewModel.widgetId = widgetIdInvalid }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetStateLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetResults(widgetIdInvalid, resultCanceled))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishViewInvalidId)

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.invalidWidgetId } returns widgetIdInvalid
            every { viewModel.resultCancelled } returns resultCanceled
            every { viewModel.viewData } returns emptyList
            every { viewModel.errorMsgEmptyList } returns emptyListError
            every { repo.getUserLists() } returns Flowable.just(emptyList)

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(widgetIdValid))

            verify { viewModel.widgetId = widgetIdValid }
            verify { viewModel.viewData = emptyList }
            assertThat(listLiveDataOutput.size).isEqualTo(4)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetStateLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetResults(widgetIdValid, resultCanceled))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.SetViewData(emptyList))
            assertThat(listLiveDataOutput[3]).isEqualTo(ViewEvents.SetStateError(emptyListError))

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.invalidWidgetId } returns widgetIdInvalid
            every { viewModel.resultCancelled } returns resultCanceled
            every { viewModel.errorMsg } returns errorMsg
            every { repo.getUserLists() } returns Flowable.error(throwable)

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(widgetIdValid))

            verify { viewModel.widgetId = widgetIdValid }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SetStateLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetResults(widgetIdValid, resultCanceled))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.SetStateError(errorMsg))

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.viewData } returns userListList
            every { viewModel.widgetId } returns widgetIdValid
            every { viewModel.resultOk } returns resultOk
            every { viewModel.sharedPrefKeyId } returns sharedPrefId
            every { viewModel.sharedPrefKeyTitle } returns sharedPrefTitle

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.SelectedUserList(position))


            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.SaveDetails(
                    userList.id, userList.title, sharedPrefId, sharedPrefTitle
            ))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SetResults(widgetIdValid, resultOk))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView(widgetIdValid))

            logic.observe().removeObserver(liveDataObserver)
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
                logic.onEvent(LogicEvents.SelectedUserList(negativePosition))
            }
        }
    }
}

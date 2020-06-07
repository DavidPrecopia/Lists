package com.precopia.david.lists.view.addedit.userlist

import androidx.lifecycle.Observer
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract.*
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [InstantExecutorExtension::class])
class AddEditUserListLogicTest {

    private val viewModel = mockk<ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val disposable = spyk<CompositeDisposable>()

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val id = "id"
    private val title = "title"
    private val position = 0

    private lateinit var logic: AddEditUserListLogic


    private val errorMessage = "error"
    private val validInput = "input"


    /**
     * I am re-instantiating the class under test before each test
     * to ensure that the observable returned by [IAddEditContract.Logic.observe]
     * is cleared before the following test.
     */
    @BeforeEach
    fun init() {
        clearAllMocks()
        logic = AddEditUserListLogic(viewModel, repo, disposable, schedulerProvider, id, title, position)
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    @Nested
    inner class ValidateInput {
        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.ADD]
         * - Get the position from the ViewModel.
         * - Add the new UserList to the repo.
         *   - This will be successful.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Add - successful`() {
            every { viewModel.taskType } returns ADD
            every { viewModel.position } returns position
            every { viewModel.currentTitle } returns title
            every { repo.addUserList(validInput, position) } answers { Completable.complete() }

            logic.onEvent(LogicEvents.Save(validInput))

            verify { repo.addUserList(validInput, position) }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.FinishView)
            }
        }

        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.ADD]
         * - Get the position from the ViewModel.
         * - Add the new UserList to the repo.
         *   - This will fail.
         * - Thrown the Exception.
         * - Display a failure message.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Add - failure`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val throwable = mockk<Throwable>(relaxed = true)

            every { viewModel.taskType } returns ADD
            every { viewModel.position } returns position
            every { viewModel.currentTitle } returns title
            every { viewModel.msgError } returns errorMessage
            every { repo.addUserList(validInput, position) } answers { Completable.error(throwable) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.Save(validInput))

            verify { repo.addUserList(validInput, position) }
            verify { throwable.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(errorMessage))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.EDIT]
         * - Get the ID from the ViewModel.
         * - Rename the UserList via the repo.
         *   - This will be successful.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Edit - successful`() {
            every { viewModel.taskType } returns EDIT
            every { viewModel.id } returns id
            every { viewModel.currentTitle } returns title
            every { repo.renameUserList(id, validInput) } answers { Completable.complete() }

            logic.onEvent(LogicEvents.Save(validInput))

            verify { repo.renameUserList(id, validInput) }
            verify(exactly = 0) { viewModel.position }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.FinishView)
            }
        }

        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.EDIT]
         * - Get the ID from the ViewModel.
         * - Rename the UserList via the repo.
         *   - This will fail.
         * - Thrown the Exception.
         * - Display a failure message.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Edit - failure`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val throwable = mockk<Throwable>(relaxed = true)

            every { viewModel.taskType } returns EDIT
            every { viewModel.id } returns id
            every { viewModel.currentTitle } returns title
            every { viewModel.msgError } returns errorMessage
            every { repo.renameUserList(id, validInput) } answers { Completable.error(throwable) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.Save(validInput))

            verify { repo.renameUserList(id, validInput) }
            verify { throwable.printStackTrace() }
            verify(exactly = 0) { viewModel.position }
            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(errorMessage))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - Validate the input.
         *   - In this test it will be empty.
         * - Set View's state error with a message from the ViewModel.
         */
        @Test
        fun `validateInput - Empty Input`() {
            val emptyInput = ""

            every { viewModel.msgEmptyTitle } returns errorMessage

            logic.onEvent(LogicEvents.Save(emptyInput))

            verify { repo wasNot Called }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.SetStateError(errorMessage))
            }
        }

        /**
         * - Validate the input.
         *   - In this test the input will not be different from the current title.
         * - Set View's state error with a message from the ViewModel.
         */
        @Test
        fun `validateInput - Unchanged Input`() {
            every { viewModel.currentTitle } returns validInput
            every { viewModel.msgTitleUnchanged } returns errorMessage

            logic.onEvent(LogicEvents.Save(validInput))

            verify { repo wasNot Called }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.SetStateError(errorMessage))
            }
        }
    }


    /**
     * - Duh
     */
    @Test
    fun getCurrentTitle() {
        every { viewModel.currentTitle } returns title

        assertThat(logic.currentTitle).isEqualTo(title)
    }
}
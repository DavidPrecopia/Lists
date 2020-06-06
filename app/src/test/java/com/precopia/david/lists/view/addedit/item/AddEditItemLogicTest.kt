package com.precopia.david.lists.view.addedit.item

import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.precopia.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AddEditItemLogicTest {

    private val view = mockk<IAddEditContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IAddEditContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val id = "id"
    private val title = "title"
    private val userListId = "id_user_list"
    private val position = 0

    private val logic = AddEditItemLogic(view, viewModel, repo, schedulerProvider, id, title, userListId, position)


    private val errorMessage = "error"
    private val validInput = "input"


    @BeforeEach
    fun setUp() {
        clearAllMocks()
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    @Nested
    inner class ValidateInput {
        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.ADD]
         * - Get the position and userListId from the ViewModel.
         * - Add the new Item to the repo.
         *   - This will succeed.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Add - success`() {
            every { viewModel.taskType } returns ADD
            every { viewModel.position } returns position
            every { viewModel.currentTitle } returns title
            every { viewModel.userListId } returns userListId
            every { repo.addItem(validInput, position, userListId) } answers { Completable.complete() }

            logic.validateInput(validInput)

            verify { view.finishView() }
        }

        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.ADD]
         * - Get the position and userListId from the ViewModel.
         * - Add the new Item to the repo.
         *   - This will fail.
         * - Throw the exception.
         * - Display a failure message.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Add - failure`() {
            val throwable = mockk<Throwable>(relaxed = true)

            every { viewModel.taskType } returns ADD
            every { viewModel.position } returns position
            every { viewModel.currentTitle } returns title
            every { viewModel.userListId } returns userListId
            every { viewModel.msgError } returns errorMessage
            every { repo.addItem(validInput, position, userListId) } answers { Completable.error(throwable) }

            logic.validateInput(validInput)

            verify { throwable.printStackTrace() }
            verify { view.displayMessage(errorMessage) }
            verify { view.finishView() }
        }

        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.EDIT]
         * - Get the ID from the ViewModel.
         * - Rename the Item via the repo.
         *   - This will succeed.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Edit - success`() {
            every { viewModel.taskType } returns EDIT
            every { viewModel.id } returns id
            every { viewModel.currentTitle } returns title
            every { repo.renameItem(id, validInput) } answers { Completable.complete() }

            logic.validateInput(validInput)

            verify { repo.renameItem(id, validInput) }
            verify { view.finishView() }
            verify(exactly = 0) { viewModel.position }
        }

        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.EDIT]
         * - Get the ID from the ViewModel.
         * - Rename the Item via the repo.
         *   - This will fail.
         * - Throw the exception.
         * - Display a failure message.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Edit - failure`() {
            val throwable = mockk<Throwable>(relaxed = true)

            every { viewModel.taskType } returns EDIT
            every { viewModel.id } returns id
            every { viewModel.currentTitle } returns title
            every { viewModel.msgError } returns errorMessage
            every { repo.renameItem(id, validInput) } answers { Completable.error(throwable) }

            logic.validateInput(validInput)

            verify { repo.renameItem(id, validInput) }
            verify { throwable.printStackTrace() }
            verify { view.displayMessage(errorMessage) }
            verify { view.finishView() }
            verify(exactly = 0) { viewModel.position }
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

            logic.validateInput(emptyInput)

            verify { view.setStateError(errorMessage) }
            verify { repo wasNot Called }
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

            logic.validateInput(validInput)

            verify { view.setStateError(errorMessage) }
            verify { repo wasNot Called }
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
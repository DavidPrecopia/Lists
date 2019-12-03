package com.example.david.lists.view.addedit.userlist

import com.example.david.lists.SchedulerProviderMockInit
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.example.domain.datamodel.UserList
import com.example.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AddEditUserListLogicTest {

    private val view = mockk<IAddEditContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IAddEditContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()

    private val id = "id"
    private val title = "title"
    private val position = 0

    private val logic = AddEditUserListLogic(view, viewModel, repo, schedulerProvider, id, title, position)


    private val errorMessage = "error"
    private val validInput = "input"


    @BeforeEach
    fun init() {
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
         * - Get the position from the ViewModel.
         * - Add the new UserList to the repo.
         *   - This will be successful.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Add - successful`() {
            val userList = UserList(validInput, position)

            every { viewModel.taskType } returns ADD
            every { viewModel.position } returns position
            every { viewModel.currentTitle } returns title
            every { repo.addUserList(userList) } answers { Completable.complete() }

            logic.validateInput(validInput)

            verify { repo.addUserList(userList) }
            verify { view.finishView() }
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
         * - Finish the View.
         */
        @Test
        fun `validateInput - Add - failure`() {
            val userList = UserList(validInput, position)
            val throwable = mockk<Throwable>(relaxed = true)

            every { viewModel.taskType } returns ADD
            every { viewModel.position } returns position
            every { viewModel.currentTitle } returns title
            every { repo.addUserList(userList) } answers { Completable.error(throwable) }

            logic.validateInput(validInput)

            verify { repo.addUserList(userList) }
            verify { throwable.printStackTrace() }
            verify { view.finishView() }
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

            logic.validateInput(validInput)

            verify { repo.renameUserList(id, validInput) }
            verify { view.finishView() }
            verify(exactly = 0) { viewModel.position }
        }

        /**
         * - Validate the input.
         *   - It will be valid.
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.EDIT]
         * - Get the ID from the ViewModel.
         * - Rename the UserList via the repo.
         *   - This will be successful.
         * - Thrown the Exception.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Edit - failure`() {
            val throwable = mockk<Throwable>(relaxed = true)

            every { viewModel.taskType } returns EDIT
            every { viewModel.id } returns id
            every { viewModel.currentTitle } returns title
            every { repo.renameUserList(id, validInput) } answers { Completable.error(throwable) }

            logic.validateInput(validInput)

            verify { repo.renameUserList(id, validInput) }
            verify { throwable.printStackTrace() }
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
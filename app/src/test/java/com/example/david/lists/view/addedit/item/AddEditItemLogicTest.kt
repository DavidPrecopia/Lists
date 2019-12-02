package com.example.david.lists.view.addedit.item

import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.example.domain.datamodel.Item
import com.example.domain.repository.IRepositoryContract
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AddEditItemLogicTest {

    private val view = mockk<IAddEditContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IAddEditContract.ViewModel>(relaxUnitFun = true)

    private val repo = mockk<IRepositoryContract.Repository>(relaxUnitFun = true)

    private val id = "id"
    private val title = "title"
    private val userListId = "id_user_list"
    private val position = 0

    private val logic = AddEditItemLogic(view, viewModel, repo, id, title, userListId, position)


    private val errorMessage = "error"
    private val input = "input"


    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }


    @Nested
    inner class Save {
        /**
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.ADD]
         * - Get the position && userListId from the ViewModel.
         * - Add the Item to the repo.
         */
        @Test
        fun `save - Add`() {
            val capturedArg = CapturingSlot<Item>()

            every { viewModel.taskType } returns ADD
            every { viewModel.position } returns position
            every { viewModel.userListId } returns userListId
            every { repo.addItem(item = capture(capturedArg)) } answers { Unit }

            logic.save(input)

            assertThat(capturedArg.captured.title).isEqualTo(input)
            assertThat(capturedArg.captured.position).isEqualTo(position)
            assertThat(capturedArg.captured.userListId).isEqualTo(userListId)
        }

        /**
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.EDIT]
         * - Rename the Item via the repo.
         */
        @Test
        fun `save - Edit`() {
            every { viewModel.taskType } returns EDIT
            every { viewModel.id } returns id

            logic.save(input)

            verify { repo.renameItem(id, input) }
            verify(exactly = 0) { viewModel.position }
        }
    }


    @Nested
    inner class ValidateInput {
        /**
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.ADD]
         * - Get the position && userListId from the ViewModel.
         * - Add the new Item to the repo.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Add`() {
            val capturedArg = CapturingSlot<Item>()

            every { viewModel.taskType } returns ADD
            every { viewModel.position } returns position
            every { viewModel.currentTitle } returns title
            every { viewModel.userListId } returns userListId

            every { repo.addItem(item = capture(capturedArg)) } answers { Unit }

            logic.validateInput(input)

            assertThat(capturedArg.captured.title).isEqualTo(input)
            assertThat(capturedArg.captured.position).isEqualTo(position)
            assertThat(capturedArg.captured.userListId).isEqualTo(userListId)
            verify { view.finishView() }
        }

        /**
         * - Get the current task type from the ViewModel.
         *   - In this test it will be [TaskType.EDIT]
         *   Get the ID from the ViewModel.
         * - Rename the Item via the repo.
         * - Finish the View.
         */
        @Test
        fun `validateInput - Edit`() {
            every { viewModel.taskType } returns EDIT
            every { viewModel.id } returns id
            every { viewModel.currentTitle } returns title

            logic.validateInput(input)

            verify { repo.renameItem(id, input) }
            verify { view.finishView() }
            verify(exactly = 0) { viewModel.position }
        }

        /**
         * - Check if the input is empty or is different from the current title.
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
         * - Check if the input
         *   - In this test it will not be different.
         * - Set View's state error with a message from the ViewModel.
         */
        @Test
        fun `validateInput - Unchanged Input`() {
            every { viewModel.currentTitle } returns input
            every { viewModel.msgTitleUnchanged } returns errorMessage

            logic.validateInput(input)

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
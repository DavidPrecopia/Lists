package com.example.david.lists.view.addedit.userlist

import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.ADD
import com.example.david.lists.view.addedit.common.IAddEditContract.TaskType.EDIT
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddEditUserListLogicTest {

    @Mock
    private lateinit var view: IAddEditContract.View

    @Mock
    private lateinit var viewModel: IAddEditContract.ViewModel

    @Mock
    private lateinit var repo: IRepositoryContract.Repository

    private val id = "id"

    private val title = "title"

    private val position = 0


    private lateinit var logic: AddEditUserListLogic


    private val errorMessage = "error"

    private val input = "input"


    @Before
    fun setUp() {
        logic = AddEditUserListLogic(
                view, viewModel, repo, id, title, position
        )
    }


    /**
     * Normal behavior - [TaskType.ADD]
     * - Add the UserList to the repo.
     */
    @Test
    fun saveAdd() {
        `when`(viewModel.taskType).thenReturn(ADD)
        `when`(viewModel.position).thenReturn(position)

        logic.save(input)

        argumentCaptor<UserList>().apply {
            verify(repo).addUserList(capture())
            assertThat(firstValue.title, `is`(input))
            assertThat(firstValue.position, `is`(position))
        }
    }

    /**
     * Normal behavior - [TaskType.EDIT]
     * - Rename the UserList via the repo.
     */
    @Test
    fun saveEdit() {
        `when`(viewModel.taskType).thenReturn(EDIT)
        `when`(viewModel.id).thenReturn(id)

        logic.save(input)

        verify(repo).renameUserList(id, input)
    }


    /**
     * Normal behavior
     * - Duh
     */
    @Test
    fun getCurrentTitle() {
        `when`(viewModel.currentTitle).thenReturn(title)

        assertThat(
                logic.currentTitle,
                `is`(title)
        )
    }


    /**
     * Normal behavior - [TaskType.ADD]
     * - Add the new UserList to the repo.
     * - Finish the View.
     */
    @Test
    fun validateInputAdd() {
        `when`(viewModel.taskType).thenReturn(ADD)
        `when`(viewModel.position).thenReturn(position)

        logic.validateInput(input)

        argumentCaptor<UserList>().apply {
            verify(repo).addUserList(capture())
            assertThat(firstValue.title, `is`(input))
            assertThat(firstValue.position, `is`(position))
        }
        verify(view).finishView()
    }

    /**
     * Normal behavior - [TaskType.EDIT]
     * - Rename the UserList via the repo.
     * - Finish the View.
     */
    @Test
    fun validateInputEdit() {
        `when`(viewModel.taskType).thenReturn(EDIT)
        `when`(viewModel.id).thenReturn(id)

        logic.validateInput(input)

        verify(repo).renameUserList(id, input)
        verify(view).finishView()
    }

    /**
     * Error behavior - empty input
     * - Set View's state error.
     */
    @Test
    fun validateInputEmptyInput() {
        val emptyInput = ""

        `when`(viewModel.msgEmptyTitle).thenReturn(errorMessage)

        logic.validateInput(emptyInput)

        verify(view).setStateError(errorMessage)
        verify(repo, never()).addUserList(any())
        verify(repo, never()).renameUserList(anyString(), anyString())
    }

    /**
     * Error behavior - unchanged input
     * - Set View's state error.
     */
    @Test
    fun validateInputUnchangedInput() {
        `when`(viewModel.currentTitle).thenReturn(input)
        `when`(viewModel.msgTitleUnchanged).thenReturn(errorMessage)

        logic.validateInput(input)

        verify(view).setStateError(errorMessage)
        verify(repo, never()).addUserList(any())
        verify(repo, never()).renameUserList(anyString(), anyString())
    }
}
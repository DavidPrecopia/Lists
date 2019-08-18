package com.example.david.lists.view.addedit.item

import com.example.david.lists.data.datamodel.Item
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
class AddEditItemLogicTest {

    @Mock
    private lateinit var view: IAddEditContract.View

    @Mock
    private lateinit var viewModel: IAddEditContract.ViewModel

    @Mock
    private lateinit var repo: IRepositoryContract.Repository

    private val id = "id"

    private val title = "title"

    private val userListId = "id_user_list"

    private val position = 0


    private lateinit var logic: AddEditItemLogic


    private val errorMessage = "error"

    private val input = "input"


    @Before
    fun setUp() {
        logic = AddEditItemLogic(
                view, viewModel, repo, id, title, userListId, position
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
        `when`(viewModel.userListId).thenReturn(userListId)

        logic.save(input)

        argumentCaptor<Item>().apply {
            verify(repo).addItem(capture())
            assertThat(firstValue.title, `is`(input))
            assertThat(firstValue.position, `is`(position))
            assertThat(firstValue.userListId, `is`(userListId))
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

        verify(repo).renameItem(id, input)
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
     * - Add the new Item to the repo.
     * - Finish the View.
     */
    @Test
    fun validateInputAdd() {
        `when`(viewModel.taskType).thenReturn(ADD)
        `when`(viewModel.position).thenReturn(position)
        `when`(viewModel.userListId).thenReturn(userListId)

        logic.validateInput(input)


        argumentCaptor<Item>().apply {
            verify(repo).addItem(capture())
            assertThat(firstValue.title, `is`(input))
            assertThat(firstValue.position, `is`(position))
            assertThat(firstValue.userListId, `is`(userListId))
        }
        verify(view).finishView()
    }

    /**
     * Normal behavior - [TaskType.EDIT]
     * - Rename the Item via the repo.
     * - Finish the View.
     */
    @Test
    fun validateInputEdit() {
        `when`(viewModel.taskType).thenReturn(EDIT)
        `when`(viewModel.id).thenReturn(id)

        logic.validateInput(input)

        verify(repo).renameItem(id, input)
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
        verify(repo, never()).addItem(any())
        verify(repo, never()).renameItem(anyString(), anyString())
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
        verify(repo, never()).addItem(any())
        verify(repo, never()).renameItem(anyString(), anyString())
    }
}
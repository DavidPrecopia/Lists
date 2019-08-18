package com.example.david.lists.data.repository

import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.remote.IRemoteRepositoryContract
import com.nhaarman.mockitokotlin2.never
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RepositoryTest {

    @Mock
    private lateinit var remoteRepo: IRemoteRepositoryContract.Repository

    @InjectMocks
    private lateinit var repo: Repository

    private val id = "qwerty"
    private val title = "title"
    private val position = 0
    private val userListId = "user_list_id"

    private val userList = UserList(title, position)

    private val item = Item(title, position, userListId)

    private val userLists = listOf(userList)
    private val items = listOf(item)

    private val newTitle = "New Title"


    /**
     * Normal behavior
     */
    @Test
    fun getAllUserLists() {
        repo.getUserLists
        verify(remoteRepo).userLists
    }

    /**
     * Normal behavior
     */
    @Test
    fun getAllItems() {
        repo.getItems(id)
        verify(remoteRepo).getItems(id)
    }


    /**
     * Normal behavior
     */
    @Test
    fun addUserList() {
        repo.addUserList(userList)
        verify(remoteRepo).addUserList(userList)
    }

    /**
     * Normal behavior
     */
    @Test
    fun addItem() {
        repo.addItem(item)
        verify(remoteRepo).addItem(item)
    }


    /**
     * Normal behavior
     */
    @Test
    fun deleteUserLists() {
        repo.deleteUserLists(userLists)

        verify(remoteRepo).deleteUserLists(userLists)
    }

    /**
     * Error behavior - empty List
     * - Throws Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun deleteUserList_EmptyList() {
        repo.deleteUserLists(listOf())
    }


    /**
     * Normal behavior
     */
    @Test
    fun deleteItems() {
        repo.deleteItems(items)

        verify(remoteRepo).deleteItems(items)
    }

    /**
     * Error behavior - empty List
     * - Throws Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun deleteItems_EmptyList() {
        repo.deleteItems(listOf())
    }


    /**
     * Normal behavior
     */
    @Test
    fun renameUserList() {
        repo.renameUserList(id, newTitle)

        verify(remoteRepo).renameUserList(id, newTitle)
    }

    /**
     * Error behavior - empty title
     * - Throws Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun renameUserList_EmptyTitle() {
        repo.renameUserList(id, "")
    }

    /**
     * Error behavior - empty ID
     * - Throws Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun renameUserList_EmptyId() {
        repo.renameUserList("", newTitle)
    }


    /**
     * Normal behavior
     */
    @Test
    fun renameItem() {
        repo.renameItem(id, newTitle)

        verify(remoteRepo).renameItem(id, newTitle)
    }

    /**
     * Error behavior - empty title
     * - Throws Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun renameItem_EmptyTitle() {
        repo.renameItem(id, "")
    }

    /**
     * Error behavior - empty ID
     * - Throws Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun renameItem_EmptyId() {
        repo.renameItem("", newTitle)
    }


    /**
     * Error behavior - empty UserListId
     * - Throws Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun getItems_EmptyUserListId() {
        repo.getItems("")
    }


    /**
     * Normal behavior
     */
    @Test
    fun updateUserListPosition() {
        val newPosition = 1
        val oldPosition = 5

        repo.updateUserListPosition(userList, oldPosition, newPosition)

        verify(remoteRepo).updateUserListPosition(userList, oldPosition, newPosition)
    }

    /***
     * Error behavior - positions are the same
     * - Method returns without invoking any other methods.
     */
    @Test
    fun updateUserListPosition_PositionsAreTheSame() {
        val position = 0

        repo.updateUserListPosition(userList, position, position)

        verify(remoteRepo, never()).updateUserListPosition(userList, position, position)
    }

    /**
     * Error behavior - both positions are negative
     * - Throws an Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun updateUserListPosition_BothPositionsAreNegative() {
        val position1 = -1
        val position2 = -10

        repo.updateUserListPosition(userList, position1, position2)
    }

    /**
     * Error behavior - first position is negative
     * - Throws an Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun updateUserListPosition_FirstPositionIsNegative() {
        val position1 = -1
        val position2 = 10

        repo.updateUserListPosition(userList, position1, position2)
    }

    /**
     * Error behavior - second position is negative
     * - Throws an Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun updateUserListPosition_SecondPositionIsNegative() {
        val position1 = 1
        val position2 = -10

        repo.updateUserListPosition(userList, position1, position2)
    }


    /**
     * Normal behavior
     */
    @Test
    fun updateItemPosition() {
        val newPosition = 1
        val oldPosition = 5

        repo.updateItemPosition(item, oldPosition, newPosition)

        verify(remoteRepo, times(1)).updateItemPosition(item, oldPosition, newPosition)
    }

    /***
     * Error behavior - positions are the same
     * - Method returns without invoking any other methods.
     */
    @Test
    fun updateItemPosition_PositionsAreTheSame() {
        val position = 0

        repo.updateItemPosition(item, position, position)

        verify(remoteRepo, never()).updateItemPosition(item, position, position)
    }

    /**
     * Error behavior - both positions are negative
     * - Throws an Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun updateItemPosition_BothPositionsAreNegative() {
        val position1 = -1
        val position2 = -10

        repo.updateItemPosition(item, position1, position2)
    }

    /**
     * Error behavior - first position is negative
     * - Throws an Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun updateItemPosition_FirstPositionIsNegative() {
        val position1 = -1
        val position2 = 10

        repo.updateItemPosition(item, position1, position2)
    }

    /**
     * Error behavior - second position is negative
     * - Throws an Exception.
     */
    @Test(expected = IllegalArgumentException::class)
    fun updateItemPosition_SecondPositionIsNegative() {
        val position1 = 1
        val position2 = -10

        repo.updateItemPosition(item, position1, position2)
    }
}
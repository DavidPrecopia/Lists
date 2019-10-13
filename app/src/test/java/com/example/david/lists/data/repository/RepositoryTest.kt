package com.example.david.lists.data.repository

import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.data.remote.IRemoteRepositoryContract
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RepositoryTest {

    private val remoteRepo = mockk<IRemoteRepositoryContract.Repository>(relaxUnitFun = true)

    private val repo = Repository(remoteRepo)

    private val id = "qwerty"
    private val title = "title"
    private val position = 0
    private val userListId = "user_list_id"

    private val userList = UserList(title, position)
    private val item = Item(title, position, userListId)


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    @Nested
    inner class Delete {
        /**
         * Empty List argument - Exception thrown
         */
        @Test
        fun `Delete UserLists - empty list`() {
            assertThrows<IllegalArgumentException> {
                repo.deleteUserLists(emptyList())
            }
        }


        /**
         * Empty List argument - Exception thrown
         */
        @Test
        fun `Delete Items - EmptyList`() {
            assertThrows<IllegalArgumentException> {
                repo.deleteItems(emptyList())
            }
        }
    }


    @Nested
    inner class Rename {
        /**
         * Empty title argument - Exception thrown.
         */
        @Test
        fun `Rename UserList - EmptyTitle`() {

            assertThrows<IllegalArgumentException> {
                repo.renameUserList(id, "")
            }
        }

        /**
         * Empty ID argument - Exception thrown.
         */
        @Test
        fun `Rename UserList - Empty ID`() {
            assertThrows<IllegalArgumentException> {
                repo.renameUserList("", title)
            }
        }


        /**
         * Empty title argument - Exception thrown.
         */
        @Test
        fun `Rename Item - Empty Title`() {
            assertThrows<IllegalArgumentException> {
                repo.renameItem(id, "")
            }
        }

        /**
         * Empty ID argument - Exception thrown.
         */
        @Test
        fun `Rename Item - Empty ID`() {
            assertThrows<IllegalArgumentException> {
                repo.renameItem("", title)
            }
        }
    }


    /**
     * Empty UserListId argument - Exception thrown.
     */
    @Test
    fun `Get Items - Empty UserListId`() {
        assertThrows<IllegalArgumentException> {
            repo.getItems("")
        }
    }


    @Nested
    inner class UserListPosition {
        @Test
        fun `Update UserList Position`() {
            val newPosition = 1
            val oldPosition = 5

            repo.updateUserListPosition(userList, oldPosition, newPosition)

            verify { remoteRepo.updateUserListPosition(userList, oldPosition, newPosition) }
        }

        /**
         * Both positions are the same - method returns without invoking the RemoteRepo.
         */
        @Test
        fun `Update UserList Position - Both Positions are the same`() {
            val position = 0

            repo.updateUserListPosition(userList, position, position)

            verify(exactly = 0) { remoteRepo.updateUserListPosition(userList, position, position) }
        }

        /**
         * Both positions are negative - Exception is thrown.
         */
        @Test
        fun updateUserListPosition_BothPositionsAreNegative() {
            val position1 = -1
            val position2 = -10

            assertThrows<IllegalArgumentException> {
                repo.updateUserListPosition(userList, position1, position2)
            }
        }

        /**
         * First position is negative - Exception is thrown.
         */
        @Test
        fun `Update UserList Position - First Position is negative`() {
            val position1 = -1
            val position2 = 10

            assertThrows<IllegalArgumentException> {
                repo.updateUserListPosition(userList, position1, position2)
            }

        }

        /**
         * Second position is negative - Exception is thrown.
         */
        @Test
        fun `Update UserList Position - Second Position is negative`() {
            val position1 = 1
            val position2 = -10

            assertThrows<IllegalArgumentException> {
                repo.updateUserListPosition(userList, position1, position2)
            }
        }
    }

    @Nested
    inner class ItemPosition {
        @Test
        fun `Update Item Position`() {
            val newPosition = 1
            val oldPosition = 5

            repo.updateItemPosition(item, oldPosition, newPosition)

            verify { remoteRepo.updateItemPosition(item, oldPosition, newPosition) }
        }

        /***
         * Both positions are the same - returns without invoking the RemoteRepo.
         */
        @Test
        fun `Update Item Position - Both Positions are the same`() {
            val position = 0

            repo.updateItemPosition(item, position, position)

            verify(exactly = 0) { remoteRepo.updateItemPosition(item, position, position) }
        }

        /**
         * Both positions are negative - Exception is thrown.
         */
        @Test
        fun `Update Item Position - Both Positions are negative`() {
            val position1 = -1
            val position2 = -10

            assertThrows<IllegalArgumentException> {
                repo.updateItemPosition(item, position1, position2)
            }
        }

        /**
         * First position is negative - Exception is thrown.
         */
        @Test
        fun updateItemPosition_FirstPositionIsNegative() {
            val position1 = -1
            val position2 = 10

            assertThrows<IllegalArgumentException> {
                repo.updateItemPosition(item, position1, position2)
            }
        }

        /**
         * Second position is negative - Exception is thrown.
         */
        @Test
        fun updateItemPosition_SecondPositionIsNegative() {
            val position1 = 1
            val position2 = -10

            assertThrows<IllegalArgumentException> {
                repo.updateItemPosition(item, position1, position2)
            }
        }
    }
}
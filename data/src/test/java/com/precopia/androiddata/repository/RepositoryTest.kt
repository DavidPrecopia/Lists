package com.precopia.androiddata.repository

import com.precopia.androiddata.datamodel.FirebaseItem
import com.precopia.androiddata.datamodel.FirebaseUserList
import com.precopia.androiddata.remote.IRemoteRepositoryContract
import com.precopia.domain.datamodel.Item
import com.precopia.domain.datamodel.UserList
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Flowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RepositoryTest {

    private val remoteRepo = mockk<IRemoteRepositoryContract.Repository>(relaxUnitFun = true)


    private val repo = Repository(remoteRepo)


    private val title = "title"
    private val position = 0
    private val id = "ID"


    @Nested
    inner class UserList {
        private val firebaseUserList = FirebaseUserList(title, position, id)
        private val userList = UserList(title, position, id)

        private val firebaseUserListList = listOf(firebaseUserList)

        /**
         * A list of [FirebaseUserList] is mapped into
         * a list of [UserList] with the same properties.
         */
        @Test
        fun getUserListDeletedObservable() {
            every {
                remoteRepo.userListDeletedObservable
            } answers {
                Flowable.just(firebaseUserListList)
            }

            repo.userListDeletedObservable
                    .test()
                    .assertValue(listOf(userList))
        }

        /**
         * A list of [FirebaseUserList] is mapped into
         * a list of [UserList] with the same properties.
         */
        @Test
        fun getUserLists() {
            every {
                remoteRepo.getUserLists()
            } answers {
                Flowable.just(firebaseUserListList)
            }

            repo.getUserLists()
                    .test()
                    .assertValue(listOf(userList))
        }
    }


    @Nested
    inner class Item {
        private val userListId = "userListId"

        private val firebaseItem = FirebaseItem(title, position, userListId, id)
        private val item = Item(title, position, userListId, id)

        private val firebaseItemList = listOf(firebaseItem)

        /**
         * A list of [FirebaseItem] is mapped into
         * a list of [Item] with the same properties.
         */
        @Test
        fun getItems() {
            every {
                remoteRepo.getItems(userListId)
            } answers {
                Flowable.just(firebaseItemList)
            }

            repo.getItems(userListId)
                    .test()
                    .assertValue(listOf(item))
        }
    }
}
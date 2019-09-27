package com.example.david.lists.data.repository

import androidx.lifecycle.LiveData

import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.data.datamodel.UserList

import io.reactivex.Flowable

interface IRepositoryContract {
    interface Repository {
        val getUserLists: Flowable<List<UserList>>

        val userListDeletedObservable: Flowable<List<UserList>>

        fun getItems(userListId: String): Flowable<List<Item>>

        fun addUserList(userList: UserList)

        fun addItem(item: Item)

        fun deleteUserLists(userListList: List<UserList>)

        fun deleteItems(itemList: List<Item>)

        fun renameUserList(id: String, newTitle: String)

        fun renameItem(id: String, newTitle: String)

        fun updateUserListPosition(userList: UserList, oldPosition: Int, newPosition: Int)

        fun updateItemPosition(item: Item, oldPosition: Int, newPosition: Int)
    }

    interface UserRepository {
        val signedOut: Boolean

        fun userSignedOutObservable(): LiveData<Boolean>
    }
}

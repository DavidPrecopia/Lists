package com.example.androiddata.remote

import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList

import io.reactivex.Flowable

interface IRemoteRepositoryContract {
    interface Repository {
        val userLists: Flowable<List<UserList>>

        val userListDeletedObservable: Flowable<List<UserList>>

        fun getItems(userListId: String): Flowable<List<Item>>

        fun addUserList(userList: UserList)

        fun addItem(item: Item)

        fun deleteUserLists(userListList: List<UserList>)

        fun deleteItems(itemList: List<Item>)

        fun renameUserList(userListId: String, newName: String)

        fun renameItem(itemId: String, newName: String)

        fun updateUserListPosition(userList: UserList, oldPosition: Int, newPosition: Int)

        fun updateItemPosition(item: Item, oldPosition: Int, newPosition: Int)
    }

    interface SnapshotListener {
        val userListFlowable: Flowable<List<UserList>>

        val deletedUserListsFlowable: Flowable<List<UserList>>

        fun getItemFlowable(userListId: String): Flowable<List<Item>>
    }
}

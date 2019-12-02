package com.example.androiddata.remote

import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList
import io.reactivex.Completable

import io.reactivex.Flowable

interface IRemoteRepositoryContract {
    interface Repository {
        fun getUserLists(): Flowable<List<UserList>>

        fun getItems(userListId: String): Flowable<List<Item>>

        val userListDeletedObservable: Flowable<List<UserList>>

        fun addUserList(userList: UserList): Completable

        fun addItem(item: Item): Completable

        fun deleteUserLists(userListList: List<UserList>): Completable

        fun deleteItems(itemList: List<Item>): Completable

        fun renameUserList(userListId: String, newName: String): Completable

        fun renameItem(itemId: String, newName: String): Completable

        fun updateUserListPosition(userList: UserList, oldPos: Int, newPos: Int): Completable

        fun updateItemPosition(item: Item, oldPos: Int, newPos: Int): Completable
    }

    interface SnapshotListener {
        val deletedUserListsFlowable: Flowable<List<UserList>>

        fun getUserListFlowable(): Flowable<List<UserList>>

        fun getItemFlowable(userListId: String): Flowable<List<Item>>
    }
}

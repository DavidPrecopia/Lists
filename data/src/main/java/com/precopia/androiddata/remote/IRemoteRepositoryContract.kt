package com.precopia.androiddata.remote

import com.precopia.androiddata.datamodel.FirebaseItem
import com.precopia.androiddata.datamodel.FirebaseUserList
import com.precopia.domain.datamodel.Item
import com.precopia.domain.datamodel.UserList
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

internal interface IRemoteRepositoryContract {
    interface Repository {
        fun getUserLists(): Flowable<List<FirebaseUserList>>

        fun getItems(userListId: String): Flowable<List<FirebaseItem>>

        val userListDeletedObservable: Flowable<List<FirebaseUserList>>

        fun addUserList(newTitle: String, position: Int): Completable

        fun addItem(newTitle: String, position: Int, userListId: String): Completable

        fun deleteUserLists(userListList: List<UserList>): Completable

        fun deleteItems(itemList: List<Item>): Completable

        fun renameUserList(id: String, newName: String): Completable

        fun renameItem(id: String, newName: String): Completable

        fun updateUserListPosition(id: String, oldPos: Int, newPos: Int): Completable

        fun updateItemPosition(id: String, userListId: String, oldPos: Int, newPos: Int): Completable
    }

    interface SnapshotListener {
        val deletedUserListsFlowable: Flowable<List<FirebaseUserList>>

        fun getUserListFlowable(): Flowable<List<FirebaseUserList>>

        fun getItemFlowable(userListId: String): Flowable<List<FirebaseItem>>
    }
}

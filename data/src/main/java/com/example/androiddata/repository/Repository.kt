package com.example.androiddata.repository

import com.example.androiddata.remote.IRemoteRepositoryContract
import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList
import com.example.domain.repository.IRepositoryContract
import io.reactivex.Flowable

class Repository(private val remote: IRemoteRepositoryContract.Repository) :
        IRepositoryContract.Repository {

    override val userListDeletedObservable: Flowable<List<UserList>>
        get() = remote.userListDeletedObservable


    override fun getUserLists() = remote.getUserLists()

    override fun getItems(userListId: String) = remote.getItems(userListId)


    override fun addUserList(userList: UserList) = remote.addUserList(userList)

    override fun addItem(item: Item) = remote.addItem(item)


    override fun deleteUserLists(userListList: List<UserList>) = remote.deleteUserLists(userListList)

    override fun deleteItems(itemList: List<Item>) = remote.deleteItems(itemList)

    override fun renameUserList(id: String, newTitle: String) = remote.renameUserList(id, newTitle)

    override fun renameItem(id: String, newTitle: String) = remote.renameItem(id, newTitle)


    override fun updateUserListPosition(userList: UserList, oldPosition: Int, newPosition: Int) =
            remote.updateUserListPosition(userList, oldPosition, newPosition)

    override fun updateItemPosition(item: Item, oldPosition: Int, newPosition: Int) =
            remote.updateItemPosition(item, oldPosition, newPosition)
}

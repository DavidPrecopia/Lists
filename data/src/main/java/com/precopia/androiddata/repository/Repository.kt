package com.precopia.androiddata.repository

import com.precopia.androiddata.datamodel.FirebaseItem
import com.precopia.androiddata.datamodel.FirebaseUserList
import com.precopia.androiddata.remote.IRemoteRepositoryContract
import com.precopia.domain.datamodel.Item
import com.precopia.domain.datamodel.UserList
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.Flowable

internal class Repository(private val remote: IRemoteRepositoryContract.Repository) :
        IRepositoryContract.Repository {

    override val userListDeletedObservable: Flowable<List<UserList>>
        get() = remote.userListDeletedObservable.map { mapRemoteUserLists(it) }


    override fun getUserLists() =
            remote.getUserLists().map { mapRemoteUserLists(it) }

    override fun getItems(userListId: String) =
            remote.getItems(userListId).map { mapRemoteItems(it) }


    override fun addUserList(newTitle: String, position: Int) =
            remote.addUserList(newTitle, position)

    override fun addItem(newTitle: String, position: Int, userListId: String) =
            remote.addItem(newTitle, position, userListId)


    override fun deleteUserLists(userListList: List<UserList>) = remote.deleteUserLists(userListList)

    override fun deleteItems(itemList: List<Item>) = remote.deleteItems(itemList)


    override fun renameUserList(id: String, newTitle: String) = remote.renameUserList(id, newTitle)

    override fun renameItem(id: String, newTitle: String) = remote.renameItem(id, newTitle)


    override fun updateUserListPosition(id: String, oldPosition: Int, newPosition: Int) =
            remote.updateUserListPosition(id, oldPosition, newPosition)

    override fun updateItemPosition(id: String, userListId: String, oldPosition: Int, newPosition: Int) =
            remote.updateItemPosition(id, userListId, oldPosition, newPosition)


    private fun mapRemoteUserLists(list: List<FirebaseUserList>) = list.map {
        UserList(it.title, it.position, it.id)
    }

    private fun mapRemoteItems(list: List<FirebaseItem>) = list.map {
        Item(it.title, it.position, it.userListId, it.id)
    }
}

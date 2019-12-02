package com.example.androiddata.repository

import com.example.androiddata.common.UtilExceptions
import com.example.androiddata.remote.IRemoteRepositoryContract
import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList

import io.reactivex.Flowable

class Repository(private val remote: IRemoteRepositoryContract.Repository) :
        IRepositoryContract.Repository {

    override val getUserLists: Flowable<List<UserList>>
        get() = remote.userLists

    override val userListDeletedObservable: Flowable<List<UserList>>
        get() = remote.userListDeletedObservable

    override fun getItems(userListId: String): Flowable<List<Item>> {
        verifyValidStrings(userListId)
        return remote.getItems(userListId)
    }


    override fun addUserList(userList: UserList) {
        remote.addUserList(userList)
    }

    override fun addItem(item: Item) {
        remote.addItem(item)
    }


    override fun deleteUserLists(userListList: List<UserList>) {
        validateList(userListList)
        remote.deleteUserLists(userListList)
    }

    override fun deleteItems(itemList: List<Item>) {
        validateList(itemList)
        remote.deleteItems(itemList)
    }


    override fun renameUserList(id: String, newTitle: String) {
        verifyValidStrings(id, newTitle)
        remote.renameUserList(id, newTitle)
    }

    override fun renameItem(id: String, newTitle: String) {
        verifyValidStrings(id, newTitle)
        remote.renameItem(id, newTitle)
    }


    override fun updateUserListPosition(userList: UserList, oldPosition: Int, newPosition: Int) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return
        }
        validatePositions(oldPosition, newPosition)
        remote.updateUserListPosition(userList, oldPosition, newPosition)
    }

    override fun updateItemPosition(item: Item, oldPosition: Int, newPosition: Int) {
        if (positionNotChanged(oldPosition, newPosition)) {
            return
        }
        validatePositions(oldPosition, newPosition)
        remote.updateItemPosition(item, oldPosition, newPosition)
    }

    private fun positionNotChanged(oldPosition: Int, newPosition: Int) =
            oldPosition == newPosition


    private fun validateList(list: List<*>) {
        if (list.isEmpty()) {
            UtilExceptions.throwException(IllegalArgumentException("List is empty"))
        }
    }

    private fun verifyValidStrings(vararg stringArray: String) {
        for (testingString in stringArray) {
            if (testingString.isEmpty()) {
                UtilExceptions.throwException(IllegalArgumentException("Empty String"))
            }
        }
    }

    private fun validatePositions(positionOne: Int, positionTwo: Int) {
        if (positionOne < 0 || positionTwo < 0) {
            UtilExceptions.throwException(IllegalArgumentException("Positions cannot be less then 0"))
        }
    }
}

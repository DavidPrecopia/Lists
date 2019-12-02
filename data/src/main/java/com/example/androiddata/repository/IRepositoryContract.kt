package com.example.androiddata.repository

import com.example.domain.constants.AuthProviders
import com.example.domain.constants.PhoneNumValidationResults
import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

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
        val email: String?

        val signedOut: Boolean

        val userVerified: Boolean

        val hasEmail: Boolean

        val emailVerified: Boolean

        val authProvider: AuthProviders

        fun sendVerificationEmail(): Completable

        fun reloadUser(): Completable

        fun signOut(): Completable

        fun validatePhoneNumber(phoneNum: String): Single<PhoneNumValidationResults>

        fun deleteGoogleUser(): Completable

        fun deleteEmailUser(password: String): Completable

        fun deletePhoneUser(verificationId: String, smsCode: String): Completable

        fun userSignedOutObservable(): Flowable<Boolean>
    }
}

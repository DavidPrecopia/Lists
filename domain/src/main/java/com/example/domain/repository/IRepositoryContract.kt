package com.example.domain.repository

import com.example.domain.constants.AuthProviders
import com.example.domain.constants.PhoneNumValidationResults
import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface IRepositoryContract {
    interface Repository {
        val userListDeletedObservable: Flowable<List<UserList>>

        fun getUserLists(): Flowable<List<UserList>>

        fun getItems(userListId: String): Flowable<List<Item>>

        fun addUserList(newTitle: String, position: Int): Completable

        fun addItem(newTitle: String, position: Int, userListId: String): Completable

        fun deleteUserLists(userListList: List<UserList>): Completable

        fun deleteItems(itemList: List<Item>): Completable

        fun renameUserList(id: String, newTitle: String): Completable

        fun renameItem(id: String, newTitle: String): Completable

        fun updateUserListPosition(id: String, oldPosition: Int, newPosition: Int): Completable

        fun updateItemPosition(id: String, userListId: String, oldPosition: Int, newPosition: Int): Completable
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

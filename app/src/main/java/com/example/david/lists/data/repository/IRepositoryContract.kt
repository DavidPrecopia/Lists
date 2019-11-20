package com.example.david.lists.data.repository

import androidx.lifecycle.LiveData
import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.data.datamodel.UserList
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.PhoneAuthProvider
import io.reactivex.Flowable

const val SMS_TIME_OUT_SECONDS = 15L

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

        val authProvider: Providers

        fun sendVerificationEmail(successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener)

        fun reloadUser(successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener)

        fun signOut(successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener)

        fun validatePhoneNumber(phoneNum: String, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks)

        fun deleteGoogleUser(successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener)

        fun deleteEmailUser(password: String, successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener)

        fun deletePhoneUser(verificationId: String, smsCode: String, successListener: OnSuccessListener<in Void>, failureListener: OnFailureListener)

        fun userSignedOutObservable(): LiveData<Boolean>
    }

    enum class Providers {
        GOOGLE,
        EMAIL,
        PHONE,
        UNKNOWN
    }
}

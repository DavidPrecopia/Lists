package com.example.david.lists.view.authentication

import android.annotation.SuppressLint
import android.app.Application

import com.example.david.lists.R
import com.example.david.lists.view.common.ViewModelBase
import com.firebase.ui.auth.ErrorCodes

class AuthViewModel(application: Application, override val requestCode: Int) :
        ViewModelBase(application),
        IAuthContract.ViewModel {

    override val msgSignInSucceed: String
        get() = getStringRes(R.string.msg_sign_in_successful)

    override val msgSignInCanceled: String
        get() = getStringRes(R.string.msg_sign_in_cancelled)

    @SuppressLint("RestrictedApi")
    override fun getMsgSignInError(errorCode: Int) =
            ErrorCodes.toFriendlyMessage(errorCode)


    override val msgSignOutSucceed: String
        get() = getStringRes(R.string.msg_signed_out_successful)

    override val msgSignOutFailed: String
        get() = getStringRes(R.string.error_msg_generic)
}

package com.example.david.lists.view.authentication

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit

import com.example.david.lists.R
import com.example.david.lists.view.common.ViewModelBase
import com.firebase.ui.auth.ErrorCodes

class AuthViewModel(application: Application,
                    private val sharedPrefs: SharedPreferences,
                    private val sharedPrefsKey: String) :
        ViewModelBase(application),
        IAuthContract.ViewModel {


    companion object {
        private const val SIGN_IN_REQUEST_CODE = 1001
        private const val MAIN_ACTIVITY_REQUEST_CODE = 1002
    }


    override val signInRequestCode: Int
        get() = SIGN_IN_REQUEST_CODE

    override val mainActivityRequestCode: Int
        get() = MAIN_ACTIVITY_REQUEST_CODE


    override var emailVerificationSent: Boolean
        get() = sharedPrefs.getBoolean(sharedPrefsKey, false)
        set(value) = sharedPrefs.edit { putBoolean(sharedPrefsKey, value) }


    override val msgSignInSucceed: String
        get() = getStringRes(R.string.msg_sign_in_successful)

    override val msgSignInCanceled: String
        get() = getStringRes(R.string.msg_sign_in_cancelled)

    override val msgReSignIn: String
        get() = getStringRes(R.string.msg_re_sign_in)

    override val msgSignInError: String
        get() = getStringRes(R.string.msg_sign_in_error)

    @SuppressLint("RestrictedApi")
    override fun getMsgSignInError(errorCode: Int) =
            ErrorCodes.toFriendlyMessage(errorCode)


    override val msgSignOutSucceed: String
        get() = getStringRes(R.string.msg_signed_out_successful)

    override val msgSignOutFailed: String
        get() = getStringRes(R.string.error_msg_generic)
}

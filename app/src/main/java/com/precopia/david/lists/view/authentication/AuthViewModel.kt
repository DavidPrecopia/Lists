package com.precopia.david.lists.view.authentication

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.core.content.edit
import com.firebase.ui.auth.ErrorCodes
import com.precopia.david.lists.R

class AuthViewModel(private val getStringRes: (Int) -> String,
                    private val sharedPrefs: SharedPreferences,
                    private val sharedPrefsKey: String) :
        IAuthContract.ViewModel {


    companion object {
        private const val SIGN_IN_REQUEST_CODE = 1001
    }


    override val signInRequestCode: Int
        get() = SIGN_IN_REQUEST_CODE


    override var emailVerificationSent: Boolean
        get() = sharedPrefs.getBoolean(sharedPrefsKey, false)
        set(value) = sharedPrefs.edit { putBoolean(sharedPrefsKey, value) }


    override val msgSignInSucceed: String
        get() = getStringRes(R.string.msg_sign_in_successful)

    override val msgSignInCanceled: String
        get() = getStringRes(R.string.msg_sign_in_cancelled)

    override val msgSignInError: String
        get() = getStringRes(R.string.msg_sign_in_error)

    @SuppressLint("RestrictedApi")
    override fun getMsgSignInError(errorCode: Int) =
            ErrorCodes.toFriendlyMessage(errorCode)


    override val msgEmailNotVerified: String
        get() = getStringRes(R.string.msg_email_not_verified)


    override val msgSignOutSucceed: String
        get() = getStringRes(R.string.msg_signed_out_successful)

    override val msgSignOutFailed: String
        get() = getStringRes(R.string.error_msg_generic)
}

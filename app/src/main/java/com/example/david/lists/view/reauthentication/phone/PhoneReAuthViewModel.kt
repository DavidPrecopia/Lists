package com.example.david.lists.view.reauthentication.phone

import android.app.Application
import com.example.david.lists.R
import com.example.david.lists.view.common.ViewModelBase

class PhoneReAuthViewModel(application: Application) :
        ViewModelBase(application),
        IPhoneReAuthContract.ViewModel {

    override var phoneNumber: String = ""


    override val msgSmsSent: String
        get() = getStringRes(R.string.msg_sms_sent)


    override val msgInvalidNum: String
        get() = getStringRes(R.string.msg_invalid_phone_number)

    override val msgGenericError: String
        get() = getStringRes(R.string.error_msg_generic)

    override val msgTryAgainLater: String
        get() = getStringRes(R.string.msg_try_again)
}
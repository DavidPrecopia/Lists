package com.example.david.lists.view.reauthentication.phone

import com.example.david.lists.R

class PhoneReAuthViewModel(private val getStringRes: (Int) -> String) :
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


    override val msgTooManyRequest: String
        get() = getStringRes(R.string.msg_too_many_request)
}
package com.example.david.lists.view.reauthentication.email

import com.example.david.lists.R

class EmailReAuthViewModel(private val getStringRes: (Int) -> String) :
        IEmailReAuthContract.ViewModel {

    override val msgInvalidPassword: String
        get() = getStringRes(R.string.msg_invalid_password)

    override val msgTooManyRequest: String
        get() = getStringRes(R.string.msg_too_many_request)


    override val msgAccountDeletionSucceed: String
        get() = getStringRes(R.string.msg_account_deletion_successful)

    override val msgAccountDeletionFailed: String
        get() = getStringRes(R.string.msg_account_deletion_failed)
}
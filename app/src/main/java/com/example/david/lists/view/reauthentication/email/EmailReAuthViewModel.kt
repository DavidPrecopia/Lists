package com.example.david.lists.view.reauthentication.email

import android.app.Application
import com.example.david.lists.R
import com.example.david.lists.view.common.ViewModelBase

class EmailReAuthViewModel(application: Application) :
        ViewModelBase(application),
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
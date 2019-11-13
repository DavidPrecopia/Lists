package com.example.david.lists.view.authentication.emailreauth

import android.app.Application
import com.example.david.lists.R
import com.example.david.lists.view.common.ViewModelBase

class EmailReAuthViewModel(application: Application) :
        ViewModelBase(application),
        IEmailReAuthContract.ViewModel {

    override val invalidPassword: String
        get() = getStringRes(R.string.msg_invalid_password)


    override val msgAccountDeletionSucceed: String
        get() = getStringRes(R.string.msg_account_deletion_successful)

    override val msgAccountDeletionFailed: String
        get() = getStringRes(R.string.msg_account_deletion_failed)
}
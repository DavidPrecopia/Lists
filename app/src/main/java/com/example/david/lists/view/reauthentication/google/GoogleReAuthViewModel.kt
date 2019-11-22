package com.example.david.lists.view.reauthentication.google

import android.app.Application
import com.example.david.lists.R
import com.example.david.lists.view.common.ViewModelBase

class GoogleReAuthViewModel(application: Application) :
        ViewModelBase(application),
        IGoogleReAuthContract.ViewModel {
    override val msgAccountDeletionSucceed: String
        get() = getStringRes(R.string.msg_account_deletion_successful)

    override val msgAccountDeletionFailed: String
        get() = getStringRes(R.string.msg_account_deletion_failed)
}
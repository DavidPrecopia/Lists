package com.precopia.david.lists.view.reauthentication.google

import com.precopia.david.lists.R

class GoogleReAuthViewModel(private val getStringRes: (Int) -> String) :
        IGoogleReAuthContract.ViewModel {
    override val msgAccountDeletionSucceed: String
        get() = getStringRes(R.string.msg_account_deletion_successful)

    override val msgAccountDeletionFailed: String
        get() = getStringRes(R.string.msg_account_deletion_failed)
}
package com.example.david.lists.view.preferences

import com.example.david.lists.R

class PreferencesViewModel(private val getStringRes: (Int) -> String) :
        IPreferencesViewContract.ViewModel {
    override val msgDeletionFailed: String
        get() = getStringRes(R.string.msg_account_deletion_failed)
}
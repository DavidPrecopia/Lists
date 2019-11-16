package com.example.david.lists.view.preferences

import android.app.Application
import com.example.david.lists.R
import com.example.david.lists.view.common.ViewModelBase

class PreferencesViewModel(application: Application) :
        ViewModelBase(application),
        IPreferencesViewContract.ViewModel {
    override val msgDeletionFailed: String
        get() = getStringRes(R.string.msg_account_deletion_failed)
}
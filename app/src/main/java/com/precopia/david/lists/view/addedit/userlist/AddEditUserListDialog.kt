package com.precopia.david.lists.view.addedit.userlist

import android.content.Context
import androidx.navigation.fragment.navArgs
import com.precopia.david.lists.view.addedit.common.AddEditDialogBase
import com.precopia.david.lists.view.addedit.userlist.buildlogic.DaggerAddEditUserListComponent

class AddEditUserListDialog : AddEditDialogBase() {

    private val args: AddEditUserListDialogArgs by navArgs()

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerAddEditUserListComponent.builder()
                .application(requireActivity().application)
                .view(this)
                .id(args.userListId)
                .title(args.userListTitle)
                .position(args.userListPosition)
                .build()
                .inject(this)
    }

    override val currentTitle: String
        get() = logic.currentTitle
}
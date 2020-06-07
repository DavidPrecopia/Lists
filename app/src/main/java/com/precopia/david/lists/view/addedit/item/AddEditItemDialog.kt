package com.precopia.david.lists.view.addedit.item

import android.content.Context
import androidx.navigation.fragment.navArgs
import com.precopia.david.lists.view.addedit.common.AddEditDialogBase
import com.precopia.david.lists.view.addedit.item.buildlogic.DaggerAddEditItemComponent

class AddEditItemDialog : AddEditDialogBase() {

    private val args: AddEditItemDialogArgs by navArgs()

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerAddEditItemComponent.builder()
                .application(requireActivity().application)
                .view(this)
                .id(args.itemId)
                .title(args.itemTitle)
                .userListId(args.userListId)
                .position(args.itemPosition)
                .build()
                .inject(this)
    }

    override val currentTitle: String
        get() = logic.currentTitle
}
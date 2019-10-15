package com.example.david.lists.view.addedit.item

import android.content.Context
import androidx.navigation.fragment.navArgs
import com.example.david.lists.view.addedit.common.AddEditDialogBase
import com.example.david.lists.view.addedit.item.buildlogic.DaggerAddEditItemDialogComponent

class AddEditItemDialog : AddEditDialogBase() {

    private val args: AddEditItemDialogArgs by navArgs()

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerAddEditItemDialogComponent.builder()
                .application(activity!!.application)
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
package com.example.david.lists.view.addedit.item

import android.content.Context
import androidx.core.os.bundleOf
import com.example.david.lists.view.addedit.common.AddEditDialogBase
import com.example.david.lists.view.addedit.item.buildlogic.DaggerAddEditItemDialogComponent

class AddEditItemDialog : AddEditDialogBase() {

    companion object {
        private const val ARG_KEY_ID = "arg_key_id"
        private const val ARG_KEY_TITLE = "arg_key_title"
        private const val ARG_KEY_USER_LIST_ID = "arg_key_user_list_id"
        private const val ARG_KEY_POSITION = "arg_key_position"

        fun getInstance(id: String, title: String, userListId: String, position: Int) =
                AddEditItemDialog().apply {
                    arguments = bundleOf(
                            ARG_KEY_ID to id,
                            ARG_KEY_TITLE to title,
                            ARG_KEY_USER_LIST_ID to userListId,
                            ARG_KEY_POSITION to position
                    )
                }
    }

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerAddEditItemDialogComponent.builder()
                .application(activity!!.application)
                .view(this)
                .id(arguments!!.getString(ARG_KEY_ID)!!)
                .title(arguments!!.getString(ARG_KEY_TITLE)!!)
                .userListId(arguments!!.getString(ARG_KEY_USER_LIST_ID)!!)
                .position(arguments!!.getInt(ARG_KEY_POSITION))
                .build()
                .inject(this)
    }

    override val currentTitle: String
        get() = logic.currentTitle
}
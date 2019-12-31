package com.precopia.david.lists.view.preferences.dialogs

import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.precopia.david.lists.R

class ConfirmSignOutDialog : ConfirmDialogBase() {
    override fun initAlertDialog(): AlertDialog {
        return AlertDialog.Builder(context!!)
                .setMessage(R.string.confirm_sign_out_dialog_message)
                .setNegativeButton(R.string.button_text_cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.button_text_yes) { _, _ -> signOut() }
                .create()
    }

    private fun signOut() {
        findNavController().navigate(ConfirmSignOutDialogDirections
                .actionConfirmSignOutDialogToAuthView(signOut = true)
        )
    }
}

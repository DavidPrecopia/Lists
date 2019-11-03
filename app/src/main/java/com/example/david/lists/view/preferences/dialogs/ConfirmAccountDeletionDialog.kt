package com.example.david.lists.view.preferences.dialogs

import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.david.lists.R

class ConfirmAccountDeletionDialog : ConfirmDialogBase() {
    override fun initAlertDialog(): AlertDialog {
        return AlertDialog.Builder(context!!)
                .setMessage(R.string.confirm_account_delete_dialog_message)
                .setNegativeButton(R.string.button_text_cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.button_text_delete_account) { _, _ -> deleteAccount() }
                .create()
    }

    private fun deleteAccount() {
        findNavController().navigate(ConfirmAccountDeletionDialogDirections
                .actionConfirmAccountDeletionDialogToAuthView(deleteAccount = true)
        )
    }
}
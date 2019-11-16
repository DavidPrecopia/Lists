package com.example.david.lists.view.preferences.dialogs

import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import com.example.david.lists.R

class ConfirmAccountDeletionDialog : ConfirmDialogBase() {


    interface DeleteAccountListener : Parcelable {
        fun deleteAccountConfirmed()
    }


    private val args: ConfirmAccountDeletionDialogArgs by navArgs()

    override fun initAlertDialog(): AlertDialog {
        return AlertDialog.Builder(context!!)
                .setMessage(R.string.confirm_account_delete_dialog_message)
                .setNegativeButton(R.string.button_text_cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.button_text_delete_account) { dialog, _ ->
                    dialog.dismiss()
                    args.listener.deleteAccountConfirmed()
                }
                .create()
    }
}
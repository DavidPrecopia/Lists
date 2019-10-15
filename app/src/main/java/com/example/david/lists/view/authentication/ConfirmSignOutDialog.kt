package com.example.david.lists.view.authentication

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.david.lists.R

internal class ConfirmSignOutDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return initAlertDialog().apply {
            // `show` need to be called first
            show()
            setButtonColors(this)
        }
    }

    private fun initAlertDialog(): AlertDialog {
        return AlertDialog.Builder(activity!!)
                .setMessage(R.string.confirm_sign_out_dialog_message)
                .setNegativeButton(R.string.button_text_cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.button_text_yes) { _, _ -> signOut() }
                .create()
    }

    private fun signOut() {
        findNavController().navigate(
                ConfirmSignOutDialogDirections.actionConfirmSignOutDialogToAuthView(true)
        )
    }

    private fun setButtonColors(alertDialog: AlertDialog) {
        setButtonTextColor(alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE))
        setButtonTextColor(alertDialog.getButton(AlertDialog.BUTTON_POSITIVE))
    }

    private fun setButtonTextColor(button: Button) {
        button.setTextColor(
                ContextCompat.getColor(context!!.applicationContext, R.color.alert_dialog_button_day_night)
        )
    }
}

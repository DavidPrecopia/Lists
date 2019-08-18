package com.example.david.lists.view.userlistlist

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.david.lists.R

internal class ConfirmSignOutDialog(private val callback: ConfirmSignOutCallback) :
        DialogFragment() {


    interface ConfirmSignOutCallback {
        fun signOutConfirmed()
    }


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
                .setPositiveButton(R.string.button_text_yes) { _, _ -> callback.signOutConfirmed() }
                .create()
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

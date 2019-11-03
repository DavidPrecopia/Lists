package com.example.david.lists.view.preferences.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.david.lists.R

abstract class ConfirmDialogBase : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return initAlertDialog().apply {
            // `show` needs to be called first
            show()
            setButtonColors(this)
        }
    }

    abstract fun initAlertDialog(): AlertDialog


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
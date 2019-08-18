package com.example.david.lists.util

import android.app.Dialog
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

/**
 * A utility class to show and hide the soft keyboard.
 */
class UtilSoftKeyboard(private val inputMethodManager: InputMethodManager) {
    fun showKeyboardInDialog(dialog: Dialog, target: View) {
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        target.requestFocus()
    }

    fun hideKeyboard(target: View) {
        inputMethodManager.hideSoftInputFromWindow(target.windowToken, 0)
    }
}

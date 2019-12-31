package com.precopia.david.lists.util

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.view.inputmethod.InputMethodManager.SHOW_FORCED

/**
 * A utility class to show and hide the soft keyboard.
 */
class UtilSoftKeyboard(private val inputMethodManager: InputMethodManager) {
    fun showKeyboardInDialog(target: View) {
        inputMethodManager.toggleSoftInput(SHOW_FORCED, 0)
        target.requestFocus()
    }

    fun hideKeyboard() {
        inputMethodManager.toggleSoftInput(HIDE_IMPLICIT_ONLY, 0)
    }
}

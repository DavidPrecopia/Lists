package com.example.david.lists.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public final class UtilSoftKeyboard {
    public UtilSoftKeyboard() {
    }

    /**
     * Show keyboard and focus to given EditText.
     */
    public void showKeyboardInDialog(Dialog dialog, EditText target) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        target.requestFocus();
    }

    public void hideKeyboard(Context context, View target) {
        InputMethodManager imm = getInputMethodManager(context);
        imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
    }


    private InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}

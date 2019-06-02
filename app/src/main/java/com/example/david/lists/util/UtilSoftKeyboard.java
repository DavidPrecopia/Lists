package com.example.david.lists.util;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * A utility class to show and hide the soft keyboard.
 */
public final class UtilSoftKeyboard {
    public UtilSoftKeyboard() {
    }

    public void showKeyboardInDialog(Dialog dialog, View target) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // This does not cause a memory leak.
        target.requestFocus();
    }

    /**
     * @param application compared to Context, this prevents a memory leak.
     */
    public void hideKeyboard(Application application, View target) {
        InputMethodManager imm = getInputMethodManager(application);
        imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
    }

    private InputMethodManager getInputMethodManager(Application application) {
        return (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}

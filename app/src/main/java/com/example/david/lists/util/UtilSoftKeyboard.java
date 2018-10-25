package com.example.david.lists.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.DialogFragment;

public final class UtilSoftKeyboard {
    private UtilSoftKeyboard() {
    }

    public static void show(DialogFragment dialogFragment) {
        getInputMethodManager(dialogFragment)
                .toggleSoftInput(
                        InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY
                );
    }

    public static void hide(DialogFragment dialogFragment) {
        getInputMethodManager(dialogFragment)
                .hideSoftInputFromWindow(
                        dialogFragment.getView().getWindowToken(),
                        0
                );
    }

    private static InputMethodManager getInputMethodManager(DialogFragment dialogFragment) {
        return (InputMethodManager)
                dialogFragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}

package com.example.david.lists.view.userlistlist;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.david.lists.R;
import com.example.david.lists.util.UtilExceptions;

final class ConfirmSignOutDialog extends DialogFragment {


    interface ConfirmSignOutCallback {
        void signOutConfirmed();
    }


    private ConfirmSignOutCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment fragment = getTargetFragment();
        if (fragment == null) {
            UtilExceptions.throwException(new IllegalStateException("Parent fragment is required"));
        }
        callback = (ConfirmSignOutCallback) fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog alertDialog = initAlertDialog();
        // This needs to be called prior to accessing the buttons
        // - otherwise the getButton method returns null.
        alertDialog.show();
        setButtonColors(alertDialog);
        return alertDialog;
    }

    private AlertDialog initAlertDialog() {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.confirm_sign_out_dialog_message)
                .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.button_text_yes, (dialog, which) -> callback.signOutConfirmed())
                .create();
    }

    private void setButtonColors(AlertDialog alertDialog) {
        setButtonTextColor(alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
        setButtonTextColor(alertDialog.getButton(AlertDialog.BUTTON_POSITIVE));
    }

    private void setButtonTextColor(Button button) {
        button.setTextColor(
                ContextCompat.getColor(getContext().getApplicationContext(), R.color.alert_dialog_button_day_night)
        );
    }
}

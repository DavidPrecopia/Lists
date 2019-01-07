package com.example.david.lists.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.example.david.lists.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public final class ConfirmSignOutDialogFragment extends DialogFragment {

    private ConfirmSignOutCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (UserListFragment) getTargetFragment();
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
                .setNegativeButton(R.string.button_text_cancel, (dialog, which) -> dialog.cancel())
                .setPositiveButton(R.string.button_text_yes, (dialog, which) -> callback.proceedWithSignOut())
                .create();
    }

    private void setButtonColors(AlertDialog alertDialog) {
        setButtonTextColor(alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE));
        setButtonTextColor(alertDialog.getButton(AlertDialog.BUTTON_POSITIVE));
    }

    private void setButtonTextColor(Button button) {
        button.setTextColor(
                getContext().getResources().getColor(R.color.alert_dialog_button_day_night)
        );
    }


    interface ConfirmSignOutCallback {
        void proceedWithSignOut();
    }
}

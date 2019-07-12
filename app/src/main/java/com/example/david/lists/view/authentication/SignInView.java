package com.example.david.lists.view.authentication;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.david.lists.R;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.authentication.buildlogic.DaggerSignInViewComponent;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT;
import static com.firebase.ui.auth.ErrorCodes.DEVELOPER_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_LINK_WRONG_DEVICE_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_MISMATCH_ERROR;
import static com.firebase.ui.auth.ErrorCodes.INVALID_EMAIL_LINK_ERROR;
import static com.firebase.ui.auth.ErrorCodes.NO_NETWORK;
import static com.firebase.ui.auth.ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED;
import static com.firebase.ui.auth.ErrorCodes.PROVIDER_ERROR;
import static com.firebase.ui.auth.ErrorCodes.UNKNOWN_ERROR;

public class SignInView extends Fragment {


    public interface SignInFragmentCallback {
        void successfullySignedIn();
    }


    @Inject
    Intent authenticationIntent;

    private static final int RESPONSE_CODE_AUTH = 100;

    private SignInFragmentCallback callback;

    public SignInView() {
    }

    public static SignInView getInstance() {
        return new SignInView();
    }

    @Override
    public void onAttach(Context context) {
        inject();
        super.onAttach(context);
        callback = (SignInFragmentCallback) getActivity();
        startAuthActivity();
    }

    private void inject() {
        DaggerSignInViewComponent.create().inject(this);
    }


    private void startAuthActivity() {
        startActivityForResult(
                authenticationIntent,
                RESPONSE_CODE_AUTH
        );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESPONSE_CODE_AUTH) {
            processAuthResult(IdpResponse.fromResultIntent(data), resultCode);
        }
    }

    private void processAuthResult(IdpResponse response, int resultCode) {
        if (resultCode == RESULT_OK) {
            successfullySignedIn();
        } else {
            errorSigningIn(response);
        }
        finishFragment();
    }

    private void successfullySignedIn() {
        toastMessage(R.string.msg_welcome_user);
        callback.successfullySignedIn();
    }

    private void errorSigningIn(IdpResponse response) {
        if (response == null) {
            toastMessage(R.string.msg_sign_in_cancelled);
        } else {
            toastMessage(toFriendlyMessage(response.getError().getErrorCode()));
        }
    }

    /**
     * This method was copied from {@link ErrorCodes} because it is a restricted API.
     */
    private String toFriendlyMessage(int code) {
        switch (code) {
            case UNKNOWN_ERROR:
                return "Unknown error";
            case NO_NETWORK:
                return "No internet connection";
            case PLAY_SERVICES_UPDATE_CANCELLED:
                return "Play Services update cancelled";
            case DEVELOPER_ERROR:
                return "Developer error";
            case PROVIDER_ERROR:
                return "Provider error";
            case ANONYMOUS_UPGRADE_MERGE_CONFLICT:
                return "User account merge conflict";
            case EMAIL_MISMATCH_ERROR:
                return "You are are attempting to sign in a different email than previously " +
                        "provided";
            case INVALID_EMAIL_LINK_ERROR:
                return "You are are attempting to sign in with an invalid email link";
            case EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR:
                return "Please enter your email to continue signing in";
            case EMAIL_LINK_WRONG_DEVICE_ERROR:
                return "You must open the email link on the same device.";
            case EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR:
                return "You must determine if you want to continue linking or complete the sign in";
            case EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR:
                return "The session associated with this sign-in request has either expired or " +
                        "was cleared";
            default:
                UtilExceptions.throwException(new IllegalArgumentException("Unknown code: " + code));
                return null;
        }
    }


    private void toastMessage(int message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void toastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void finishFragment() {
        getFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }
}

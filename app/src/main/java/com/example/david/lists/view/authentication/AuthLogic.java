package com.example.david.lists.view.authentication;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.david.lists.util.UtilExceptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import static android.app.Activity.RESULT_OK;
import static com.example.david.lists.view.authentication.IAuthContract.AuthResult.AUTH_CANCELLED;
import static com.example.david.lists.view.authentication.IAuthContract.AuthResult.AUTH_SUCCESS;
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

public class AuthLogic implements IAuthContract.Logic {

    private final IAuthContract.View view;
    private final IAuthContract.ViewModel viewModel;

    private final IAuthContract.AuthGoal authGoal;

    private final Application application;
    private final AuthUI authUi;

    public AuthLogic(IAuthContract.View view,
                     IAuthContract.ViewModel viewModel,
                     IAuthContract.AuthGoal authGoal,
                     Application application,
                     AuthUI authUi) {
        this.view = view;
        this.viewModel = viewModel;
        this.authGoal = authGoal;
        this.application = application;
        this.authUi = authUi;
        init();
    }

    /**
     * If the user cancels.
     */
    private void init() {
        view.setResult(viewModel.getIntent(AUTH_CANCELLED));
    }


    @Override
    public void onStart() {
        switch (authGoal) {
            case SIGN_IN:
            case AUTH_ANON:
                view.signIn(viewModel.getAuthRequestCode());
                break;
            case SIGN_OUT:
                signOut();
                break;
        }
    }


    @Override
    public void signInResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == viewModel.getAuthRequestCode()) {
            evalSignInResult(IdpResponse.fromResultIntent(data), resultCode);
        } else {
            UtilExceptions.throwException(new IllegalArgumentException("Unknown request code"));
        }
    }

    private void evalSignInResult(IdpResponse response, int resultCode) {
        if (resultCode == RESULT_OK) {
            signInSuccessful();
        } else {
            signInFailed(response);
        }
    }

    private void signInSuccessful() {
        finish(viewModel.getMsgSignInSucceed(), viewModel.getIntent(AUTH_SUCCESS));
    }

    private void signInFailed(IdpResponse response) {
        if (response == null) {
            finish(viewModel.getMsgSignInCanceled(), viewModel.getIntent(AUTH_CANCELLED));
        } else {
            String reason = toFriendlyMessage(response.getError().getErrorCode());
            finish(reason, viewModel.getIntentFailed(reason));
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


    private void signOut() {
        authUi.signOut(application)
                .addOnSuccessListener(aVoid -> signOutSucceeded())
                .addOnFailureListener(this::signOutFailed);
    }

    private void signOutSucceeded() {
        finish(viewModel.getMsgSignOutSucceed(), viewModel.getIntent(AUTH_SUCCESS));
    }

    private void signOutFailed(Exception e) {
        UtilExceptions.throwException(e);
        finish(viewModel.getMsgSignOutFailed(), viewModel.getIntentFailed(viewModel.getMsgSignOutFailed()));
    }


    private void finish(String message, Intent resultIntent) {
        view.displayMessage(message);
        view.setResult(resultIntent);
        view.finishView();
    }
}

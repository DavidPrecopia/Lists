package com.example.david.lists.view.authentication;

import android.content.Intent;

import androidx.annotation.Nullable;

public interface IAuthContract {
    interface View {
        void signIn(int responseCode);

        void displayMessage(String message);

        void setResult(Intent intent);

        void finishView();
    }

    interface Logic {
        void onStart();

        void signInResult(int requestCode, int resultCode, @Nullable Intent data);
    }

    interface ViewModel {
        Intent getIntent(IAuthContract.AuthResult result);

        Intent getIntentFailed(String reason);

        int getAuthRequestCode();

        String getMsgSignInSucceed();

        String getMsgSignInCanceled();

        String getMsgSignOutSucceed();

        String getMsgSignOutFailed();

        String getMsgSignInWhenNotAnon();
    }


    enum AuthResult {
        AUTH_SUCCESS,
        AUTH_FAILED,
        // the user cancelled.
        AUTH_CANCELLED
    }

    // Need better name.
    enum AuthGoal {
        SIGN_IN,
        SIGN_OUT,
        // Authenticate an anonymous user.
        AUTH_ANON
    }
}

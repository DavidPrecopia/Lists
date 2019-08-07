package com.example.david.lists.view.authentication;

public interface IAuthContract {
    interface View {
        void signIn(int requestCode);

        void signOut();

        void displayMessage(String message);

        void setResult(IAuthContract.AuthResult result);

        void setResultFailed(String reason);

        void finishView();
    }

    interface Logic {
        void onStart(IAuthContract.AuthGoal authGoal);

        void signInSuccessful();

        void signInCancelled();

        void signInFailed(int errorCode);

        void signOutSucceeded();

        void signOutFailed(Exception e);
    }

    interface ViewModel {
        int getRequestCode();

        String getMsgSignInSucceed();

        String getMsgSignInCanceled();

        String getMsgSignOutSucceed();

        String getMsgSignInError(int errorCode);

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

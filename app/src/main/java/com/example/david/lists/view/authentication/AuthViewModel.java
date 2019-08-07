package com.example.david.lists.view.authentication;

import android.annotation.SuppressLint;
import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.view.common.ViewModelBase;
import com.firebase.ui.auth.ErrorCodes;

public class AuthViewModel extends ViewModelBase
        implements IAuthContract.ViewModel {

    private final int requestCode;

    public AuthViewModel(Application application, int requestCode) {
        super(application);
        this.requestCode = requestCode;
    }


    @Override
    public int getRequestCode() {
        return requestCode;
    }


    @Override
    public String getMsgSignInSucceed() {
        return getStringRes(R.string.msg_sign_in_successful);
    }

    @Override
    public String getMsgSignInCanceled() {
        return getStringRes(R.string.msg_sign_in_cancelled);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public String getMsgSignInError(int errorCode) {
        return ErrorCodes.toFriendlyMessage(errorCode);
    }


    @Override
    public String getMsgSignOutSucceed() {
        return getStringRes(R.string.msg_signed_out_successful);
    }

    @Override
    public String getMsgSignOutFailed() {
        return getStringRes(R.string.error_msg_generic);
    }

    @Override
    public String getMsgSignInWhenNotAnon() {
        return getStringRes(R.string.error_msg_sign_in_when_not_anonymous);
    }
}

package com.example.david.lists.view.authentication;

import android.app.Application;

import com.example.david.lists.R;

public class AuthViewModel implements IAuthContract.ViewModel {

    private final Application application;

    private final int requestCode;

    public AuthViewModel(Application application, int requestCode) {
        this.application = application;
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


    private String getStringRes(int resId) {
        return application.getString(resId);
    }
}

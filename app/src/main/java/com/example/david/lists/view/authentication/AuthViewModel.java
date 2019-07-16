package com.example.david.lists.view.authentication;

import android.app.Application;
import android.content.Intent;

import com.example.david.lists.R;

public class AuthViewModel implements IAuthContract.ViewModel {

    private final Application application;

    private static final int RESPONSE_CODE = 100;

    public AuthViewModel(Application application) {
        this.application = application;
    }


    @Override
    public Intent getIntent(IAuthContract.AuthResult result) {
        Intent intent = new Intent();
        intent.putExtra(getStringRes(R.string.intent_extra_auth_result), result);
        return intent;
    }

    @Override
    public Intent getIntentFailed(String failureReason) {
        Intent intent = getIntent(IAuthContract.AuthResult.AUTH_FAILED);
        intent.putExtra(getStringRes(R.string.intent_extra_auth_failure_reason), failureReason);
        return intent;
    }


    @Override
    public int getAuthRequestCode() {
        return RESPONSE_CODE;
    }


    @Override
    public String getMsgSignInSucceed() {
        return getStringRes(R.string.msg_sign_in_success);
    }

    @Override
    public String getMsgSignInCanceled() {
        return getStringRes(R.string.msg_sign_in_cancelled);
    }

    @Override
    public String getMsgSignOutSucceed() {
        return getStringRes(R.string.msg_signed_out_success);
    }

    @Override
    public String getMsgSignOutFailed() {
        return getStringRes(R.string.error_msg_generic);
    }


    private String getStringRes(int resId) {
        return application.getString(resId);
    }
}

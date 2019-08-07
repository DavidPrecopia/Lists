package com.example.david.lists.view.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.david.lists.R;
import com.example.david.lists.view.authentication.IAuthContract.AuthResult;
import com.example.david.lists.view.authentication.buildlogic.DaggerAuthViewComponent;
import com.example.david.lists.view.common.ActivityBase;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * The result code will always be {@link Activity#RESULT_OK},
 * see the Intent's extras for more information - see {@link AuthResult}.
 * <p>
 * If authentication failed, the returned Intent will contain the reason why.
 * <p>
 * This is an Activity, instead of a Fragment, because Firebase Auth depends
 * upon {@link Activity#onActivityResult(int, int, Intent)} and this
 * needs to return an Intent to its caller.
 */
public class AuthView extends ActivityBase
        implements IAuthContract.View {

    @Inject
    IAuthContract.Logic logic;

    @Inject
    Provider<Intent> authIntent;

    @Inject
    AuthUI authUi;

    private int authRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inject();
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.auth_view);
        logic.onStart(getAuthGoal());
    }

    private void inject() {
        DaggerAuthViewComponent.builder()
                .application(getApplication())
                .activity(this)
                .view(this)
                .build()
                .inject(this);
    }

    private IAuthContract.AuthGoal getAuthGoal() {
        return (IAuthContract.AuthGoal) getIntent()
                .getExtras()
                .getSerializable(getString(R.string.intent_extra_auth));
    }


    @Override
    public void signIn(int requestCode) {
        this.authRequestCode = requestCode;
        startActivityForResult(
                authIntent.get(),
                requestCode
        );
    }

    @Override
    public void signOut() {
        authUi.signOut(getApplicationContext())
                .addOnSuccessListener(aVoid -> logic.signOutSucceeded())
                .addOnFailureListener(logic::signOutFailed);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == authRequestCode) {
            evalSignInResult(IdpResponse.fromResultIntent(data), resultCode);
        }
    }

    private void evalSignInResult(IdpResponse response, int resultCode) {
        if (resultCode == RESULT_OK) {
            logic.signInSuccessful();
        } else if (response == null) {
            logic.signInCancelled();
        } else {
            logic.signInFailed(response.getError().getErrorCode());
        }
    }


    @Override
    public void displayMessage(String message) {
        super.toastMessage(message);
    }


    @Override
    public void setResult(IAuthContract.AuthResult result) {
        setResult(RESULT_OK, getResultIntent(result));
    }

    @Override
    public void setResultFailed(String failureReason) {
        Intent intent = getResultIntent(IAuthContract.AuthResult.AUTH_FAILED);
        intent.putExtra(getString(R.string.intent_extra_auth_failure_reason), failureReason);
        setResult(RESULT_OK, intent);
    }

    private Intent getResultIntent(AuthResult result) {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.intent_extra_auth_result), result);
        return intent;
    }


    @Override
    public void finishView() {
        finish();
    }
}

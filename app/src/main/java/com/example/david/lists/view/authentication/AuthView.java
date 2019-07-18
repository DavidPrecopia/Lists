package com.example.david.lists.view.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.david.lists.R;
import com.example.david.lists.view.authentication.IAuthContract.AuthResult;
import com.example.david.lists.view.authentication.buildlogic.DaggerAuthViewComponent;
import com.example.david.lists.view.common.ActivityBase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inject();
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.auth_view);
        logic.onStart();
    }

    private void inject() {
        DaggerAuthViewComponent.builder()
                .application(getApplication())
                .activity(this)
                .view(this)
                .authGoal(getAuthGoal())
                .build()
                .inject(this);
    }

    private IAuthContract.AuthGoal getAuthGoal() {
        return (IAuthContract.AuthGoal) getIntent()
                .getExtras()
                .getSerializable(getString(R.string.intent_extra_auth));
    }


    @Override
    public void signIn(int responseCode) {
        startActivityForResult(
                authIntent.get(),
                responseCode
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        logic.signInResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void setResult(Intent intent) {
        setResult(RESULT_OK, intent);
    }

    @Override
    public void finishView() {
        finish();
    }
}

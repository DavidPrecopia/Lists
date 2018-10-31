package com.example.david.lists.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.david.lists.R;
import com.example.david.lists.databinding.ActivityAuthenticateBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import timber.log.Timber;

/**
 * This Activity is used to handle a User authentication with FirebaseUI Auth.
 * Because FirebaseUI Auth returns results via the method
 * `onActivityResult()` this class needs to be an Activity.
 */
public class AuthenticateActivity extends AppCompatActivity {

    private ActivityAuthenticateBinding binding;

    private FirebaseAuth auth;
    private static final int RESPONSE_CODE_AUTH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authenticate);
        auth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        if (userIsAuthenticated()) {
            finish();
        }
        startAuthUi();
    }

    private boolean userIsAuthenticated() {
        return auth.getCurrentUser() != null;
    }


    private void startAuthUi() {
        startActivityForResult(
                getAuthIntent(),
                RESPONSE_CODE_AUTH
        );
    }

    private Intent getAuthIntent() {
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(getProviders())
                .enableAnonymousUsersAutoUpgrade()
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.mipmap.ic_launcher_round)
                .setTheme(R.style.FirebaseUIAuthStyle)
                .build();
    }

    private List<AuthUI.IdpConfig> getProviders() {
        return Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build()
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESPONSE_CODE_AUTH) {
            processAuthResult(IdpResponse.fromResultIntent(data), resultCode);
        }
    }

    private void processAuthResult(IdpResponse response, int resultCode) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Successfully signed-in", Toast.LENGTH_LONG).show();
//            Snackbar.make(binding.rootLayout, "Successfully signed-in", Snackbar.LENGTH_LONG).show();
        } else if (response == null) {
            Timber.d("User hit back button - message to user, \"Sign-in cancelled\"");
        } else {
            Timber.d(String.valueOf(response.getError().getErrorCode()));
        }

        finish();
    }
}

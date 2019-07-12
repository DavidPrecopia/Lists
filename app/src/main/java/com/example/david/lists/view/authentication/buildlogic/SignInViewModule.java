package com.example.david.lists.view.authentication.buildlogic;

import android.content.Intent;

import com.example.david.lists.R;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
final class SignInViewModule {
    @ViewScope
    @Provides
    Intent authenticationIntent() {
        return getAuthIntent();
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
}

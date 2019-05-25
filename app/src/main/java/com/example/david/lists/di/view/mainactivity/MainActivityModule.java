package com.example.david.lists.di.view.mainactivity;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.david.lists.R;
import com.example.david.lists.application.MyApplication;
import com.example.david.lists.di.view.ViewScope;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
final class MainActivityModule {
    @ViewScope
    @Provides
    FragmentManager fragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @ViewScope
    @Provides
    SharedPreferences sharedPreferences(AppCompatActivity activity) {
        return ((MyApplication) activity.getApplication())
                .getAppComponent()
                .sharedPrefsNightMode();
    }

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

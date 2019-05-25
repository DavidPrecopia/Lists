package com.example.david.lists.di.view.mainactivity;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentManager;

import com.example.david.lists.R;
import com.example.david.lists.application.MyApplication;
import com.example.david.lists.di.view.ViewScope;
import com.example.david.lists.ui.MainActivity;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
class MainActivityModule {
    @ViewScope
    @Provides
    FragmentManager fragmentManager(MainActivity mainActivity) {
        return mainActivity.getSupportFragmentManager();
    }

    @ViewScope
    @Provides
    SharedPreferences sharedPreferences(MainActivity mainActivity) {
        return ((MyApplication) mainActivity.getApplication())
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

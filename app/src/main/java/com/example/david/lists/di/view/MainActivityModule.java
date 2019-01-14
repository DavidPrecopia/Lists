package com.example.david.lists.di.view;

import android.content.Intent;
import android.content.SharedPreferences;

import com.example.david.lists.R;
import com.example.david.lists.application.MyApplication;
import com.example.david.lists.databinding.ActivityMainBinding;
import com.example.david.lists.ui.view.MainActivity;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import dagger.Module;
import dagger.Provides;

@Module
class MainActivityModule {
    @MainActivityScope
    @Provides
    ActivityMainBinding binding(MainActivity mainActivity) {
        return DataBindingUtil.setContentView(mainActivity, R.layout.activity_main);
    }

    @MainActivityScope
    @Provides
    FragmentManager fragmentManager(MainActivity mainActivity) {
        return mainActivity.getSupportFragmentManager();
    }

    @MainActivityScope
    @Provides
    SharedPreferences sharedPreferences(MainActivity mainActivity) {
        return ((MyApplication) mainActivity.getApplication())
                .getAppComponent()
                .getSharedPrefsNightMode();
    }

    @MainActivityScope
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

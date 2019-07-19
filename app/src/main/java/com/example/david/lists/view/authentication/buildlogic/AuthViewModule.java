package com.example.david.lists.view.authentication.buildlogic;

import android.app.Application;
import android.content.Intent;

import com.example.david.lists.R;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.authentication.AuthLogic;
import com.example.david.lists.view.authentication.AuthViewModel;
import com.example.david.lists.view.authentication.IAuthContract;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
class AuthViewModule {
    @ViewScope
    @Provides
    IAuthContract.Logic logic(IAuthContract.View view,
                              IAuthContract.ViewModel viewModel,
                              IRepositoryContract.UserRepository userRepo,
                              IAuthContract.AuthGoal authGoal,
                              Application application,
                              AuthUI authUi) {
        return new AuthLogic(view, viewModel, userRepo, authGoal, application, authUi);
    }

    @ViewScope
    @Provides
    IAuthContract.ViewModel viewModel(Application application) {
        return new AuthViewModel(application);
    }

    @ViewScope
    @Provides
    AuthUI authUi() {
        return AuthUI.getInstance();
    }


    @ViewScope
    @Provides
    Intent authIntent() {
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

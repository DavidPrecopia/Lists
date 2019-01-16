package com.example.david.lists.di.view;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.viewmodels.UserListViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
class UserListFragmentModule {
    @UserListFragmentScope
    @Provides
    UserListViewModelFactory viewModelFactory(Application application, IModelContract model, SharedPreferences sharedPrefs) {
        return new UserListViewModelFactory(application, model, sharedPrefs);
    }

    @UserListFragmentScope
    @Provides
    IModelContract model(Application application) {
        return ((MyApplication) application).getAppComponent().getModel();
    }

    @UserListFragmentScope
    @Provides
    SharedPreferences sharedPreferences(Application application) {
        return ((MyApplication) application).getAppComponent().getSharedPrefsNightMode();
    }
}

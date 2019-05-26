package com.example.david.lists.di.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.david.lists.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
final class SharedPreferencesModule {
    @Singleton
    @Provides
    SharedPreferences sharedPreferences(Application application) {
        return application.getSharedPreferences(
                application.getString(R.string.night_mode_shared_pref_name),
                Context.MODE_PRIVATE
        );
    }
}

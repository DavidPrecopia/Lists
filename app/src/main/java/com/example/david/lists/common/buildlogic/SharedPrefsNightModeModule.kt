package com.example.david.lists.common.buildlogic

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.david.lists.R
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SharedPrefsNightModeModule {
    @Singleton
    @Provides
    fun sharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
                application.getString(R.string.night_mode_shared_pref_name),
                Context.MODE_PRIVATE
        )
    }
}

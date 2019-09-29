package com.example.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import com.example.david.lists.R
import com.example.david.lists.util.IUtilNightModeContract
import com.example.david.lists.util.UtilNightMode
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilNightModeModule {
    @Singleton
    @Provides
    fun utilNightMode(application: Application,
                      sharedPrefs: SharedPreferences): IUtilNightModeContract {
        return UtilNightMode(
                sharedPrefs,
                application.getString(R.string.night_mode_shared_pref_key)
        )
    }
}
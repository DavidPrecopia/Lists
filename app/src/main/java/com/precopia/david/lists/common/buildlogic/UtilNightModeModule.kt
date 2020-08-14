package com.precopia.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import com.precopia.david.lists.R
import com.precopia.david.lists.util.IUtilThemeContract
import com.precopia.david.lists.util.UtilTheme
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilNightModeModule {
    @Singleton
    @Provides
    fun utilNightMode(application: Application,
                      sharedPrefs: SharedPreferences): IUtilThemeContract {
        return UtilTheme(
                sharedPrefs,
                application,
                application.getString(R.string.night_mode_shared_pref_key)
        )
    }
}
package com.precopia.david.lists.util

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class UtilNightMode(private val sharedPrefs: SharedPreferences,
                    private val application: Application,
                    private val key: String) : IUtilNightModeContract {


    override fun isNightModeEnabled(): Boolean = when (getMode()) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }

    private fun getMode() = application.resources?.configuration?.uiMode
            ?.and(Configuration.UI_MODE_NIGHT_MASK)


    override fun setDay() {
        setMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun setNight() {
        setMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    override fun setFollowSystem() {
        setMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun restore() {
        AppCompatDelegate.setDefaultNightMode(
                sharedPrefs.getInt(
                        key,
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                ))
    }


    private fun setMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        saveChange(mode)
    }

    private fun saveChange(mode: Int) {
        sharedPrefs.edit { putInt(key, mode) }
    }
}

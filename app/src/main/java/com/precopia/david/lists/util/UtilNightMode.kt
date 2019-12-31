package com.precopia.david.lists.util

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class UtilNightMode(private val sharedPrefs: SharedPreferences,
                    private val key: String) : IUtilNightModeContract {

    override val nightModeEnabled: Boolean
        get() = AppCompatDelegate.MODE_NIGHT_YES == sharedPrefs.getInt(key, -1)


    override fun setDay() {
        setMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun setNight() {
        setMode(AppCompatDelegate.MODE_NIGHT_YES)
    }


    private fun setMode(mode: Int) {
        saveChange(mode)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun saveChange(mode: Int) {
        sharedPrefs.edit { putInt(key, mode) }
    }
}

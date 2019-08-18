package com.example.david.lists.util

import android.app.Application
import android.content.SharedPreferences

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

import com.example.david.lists.R
import com.example.david.lists.common.ListsApplication

class UtilNightMode(private val application: Application) : IUtilNightModeContract {

    override val nightModeEnabled: Boolean
        get() = AppCompatDelegate.MODE_NIGHT_YES == sharedPrefs.getInt(key, -1)

    private val sharedPrefs: SharedPreferences
        get() = (application as ListsApplication).appComponent.sharedPrefsNightMode()

    private val key: String
        get() = application.getString(R.string.night_mode_shared_pref_key)


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

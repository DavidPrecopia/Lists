package com.example.david.lists.view.common

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.david.lists.R
import com.example.david.lists.view.common.buildlogic.DaggerMainActivityComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.activity_main),
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var sharedPrefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

    private fun inject() {
        DaggerMainActivityComponent.builder()
                .application(application)
                .build()
                .inject(this)
    }


    override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
        if (key == getString(R.string.night_mode_shared_pref_key)) {
            recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
    }
}
package com.precopia.david.lists.view.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.precopia.david.lists.R

class PreferencesViewFrag: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_view, rootKey)
    }
}
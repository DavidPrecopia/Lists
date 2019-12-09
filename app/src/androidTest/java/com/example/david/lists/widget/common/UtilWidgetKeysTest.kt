package com.example.david.lists.widget.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.david.lists.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verify the correct values are retrieved.
 */
@RunWith(AndroidJUnit4::class)
class UtilWidgetKeysTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val utilWidgetKeys = UtilWidgetKeys(
            { context.getString(it) },
            { res, arg -> context.getString(res, arg) }
    )

    private val maxValue = Integer.MAX_VALUE
    private val minValue = Integer.MIN_VALUE


    @Test
    fun sharedPrefsName() {
        assertThat(utilWidgetKeys.getSharedPrefName())
                .isEqualTo(context.getString(R.string.widget_shared_prefs_name))
    }


    @Test
    fun sharedPrefsKeyIdMaxValue() {
        sharedPrefsKeyId(maxValue)
    }

    @Test
    fun sharedPrefsKeyIdMinValue() {
        sharedPrefsKeyId(minValue)
    }

    private fun sharedPrefsKeyId(value: Int) {
        assertThat(utilWidgetKeys.getSharedPrefKeyId(value))
                .isEqualTo(context.getString(R.string.widget_key_shared_pref_user_list_id, value))
    }


    @Test
    fun sharedPrefsKeyTitleMaxValue() {
        sharedPrefsKeyTitle(maxValue)
    }

    @Test
    fun sharedPrefsKeyTitleMinValue() {
        sharedPrefsKeyTitle(minValue)
    }

    private fun sharedPrefsKeyTitle(value: Int) {
        assertThat(utilWidgetKeys.getSharedPrefKeyTitle(value))
                .isEqualTo(context.getString(R.string.widget_key_shared_pref_user_list_title, value))
    }
}
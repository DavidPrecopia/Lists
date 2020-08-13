package com.precopia.david.lists.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.precopia.david.lists.common.ListsApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilNightModeTest {

    private val utilNightMode = (ApplicationProvider.getApplicationContext() as ListsApplication)
            .appComponent.utilNightMode()

    @Test
    fun setDayTest() {
        utilNightMode.setDay()
        assertModeEnabled(AppCompatDelegate.MODE_NIGHT_NO)
    }

    @Test
    fun setNightTest() {
        utilNightMode.setNight()
        assertModeEnabled(AppCompatDelegate.MODE_NIGHT_YES)
    }

    @Test
    fun setFollowSystemTest() {
        utilNightMode.setFollowSystem()
        assertModeEnabled(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    @Test
    fun restoreTest() {
        utilNightMode.setNight()
        utilNightMode.restore()
        assertModeEnabled(AppCompatDelegate.MODE_NIGHT_YES)
    }


    private fun assertModeEnabled(expectation: Int) {
        assertThat(AppCompatDelegate.getDefaultNightMode()).isEqualTo(expectation)
    }
}
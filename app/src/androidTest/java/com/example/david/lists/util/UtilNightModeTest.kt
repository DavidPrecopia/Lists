package com.example.david.lists.util

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.david.lists.common.ListsApplication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilNightModeTest {

    private val utilNightMode = (ApplicationProvider.getApplicationContext() as ListsApplication)
            .appComponent.utilNightMode()

    @Test
    fun setDayTest() {
        utilNightMode.setDay()
        assertNightModeEnabled(false)
    }

    @Test
    fun setNightTest() {
        utilNightMode.setNight()
        assertNightModeEnabled(true)
    }

    private fun assertNightModeEnabled(expectation: Boolean) {
        assertThat(
                utilNightMode.nightModeEnabled,
                `is`(expectation)
        )
    }
}
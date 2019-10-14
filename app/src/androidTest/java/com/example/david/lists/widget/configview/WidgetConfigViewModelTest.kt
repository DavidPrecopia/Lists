package com.example.david.lists.widget.configview

import android.app.Activity
import android.app.Application
import android.appwidget.AppWidgetManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.david.lists.widget.UtilWidgetKeys
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This will only test some of [IWidgetConfigContract.ViewModel] methods.
 * I feel testing all of the getters and setter would be overkill because they are
 * simple one-line methods that you can verify by looking at them.
 *
 * I am testing these methods because the values they return are from the Android framework
 * and will be used the system, thus I am ensuring the correct values are being returned.
 */
@RunWith(AndroidJUnit4::class)
class WidgetConfigViewModelTest {

    private val application = ApplicationProvider.getApplicationContext() as Application

    private val viewModel = WidgetConfigViewModel(application)

    private val widgetId = 100


    @Test
    fun getInvalidWidgetId() {
        assertThat(viewModel.invalidWidgetId)
                .isEqualTo(AppWidgetManager.INVALID_APPWIDGET_ID)
    }


    @Test
    fun getResultOk() {
        assertThat(viewModel.resultOk)
                .isEqualTo(Activity.RESULT_OK)
    }

    @Test
    fun getResultCancelled() {
        assertThat(viewModel.resultCancelled)
                .isEqualTo(Activity.RESULT_CANCELED)
    }


    @Test
    fun getSharedPrefKeyId() {
        viewModel.widgetId = widgetId

        assertThat(viewModel.sharedPrefKeyId)
                .isEqualTo(UtilWidgetKeys.getSharedPrefKeyId(application, widgetId))
    }

    @Test
    fun getSharedPrefKeyTitle() {
        viewModel.widgetId = widgetId

        assertThat(viewModel.sharedPrefKeyTitle)
                .isEqualTo(UtilWidgetKeys.getSharedPrefKeyTitle(application, widgetId))
    }
}
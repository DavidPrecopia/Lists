package com.example.david.lists.widget.configview;

import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.david.lists.widget.UtilWidgetKeys;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This will only test some of {@link IWidgetConfigContract.ViewModel} methods.
 * I feel testing all of the getters and setter would be overkill because they are
 * simple one-line methods that you can verify by looking at them.
 * <p>
 * I am testing these methods because the values they return are from the Android framework
 * and will be used the system, thus I am ensuring the correct values are being returned.
 */
@RunWith(AndroidJUnit4.class)
public class WidgetConfigViewModelTest {

    private WidgetConfigViewModel viewModel;

    private Application application;

    private int widgetId = 100;


    @Before
    public void setUp() {
        application = ApplicationProvider.getApplicationContext();
        viewModel = new WidgetConfigViewModel(application);
    }


    @Test
    public void getInvalidWidgetId() {
        assertThat(
                viewModel.getInvalidWidgetId(),
                is(AppWidgetManager.INVALID_APPWIDGET_ID)
        );
    }


    @Test
    public void getResultOk() {
        assertThat(
                viewModel.getResultOk(),
                is(Activity.RESULT_OK)
        );
    }

    @Test
    public void getResultCancelled() {
        assertThat(
                viewModel.getResultCancelled(),
                is(Activity.RESULT_CANCELED)
        );
    }


    @Test
    public void getSharedPrefKeyId() {
        viewModel.setWidgetId(widgetId);
        assertThat(
                viewModel.getSharedPrefKeyId(),
                is(UtilWidgetKeys.getSharedPrefKeyId(application, widgetId))
        );
    }

    @Test
    public void getSharedPrefKeyTitle() {
        viewModel.setWidgetId(widgetId);
        assertThat(
                viewModel.getSharedPrefKeyTitle(),
                is(UtilWidgetKeys.getSharedPrefKeyTitle(application, widgetId))
        );
    }
}
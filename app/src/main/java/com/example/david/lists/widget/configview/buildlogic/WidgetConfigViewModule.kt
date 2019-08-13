package com.example.david.lists.widget.configview.buildlogic

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Intent
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.widget.configview.IWidgetConfigContract
import com.example.david.lists.widget.configview.WidgetConfigAdapter
import com.example.david.lists.widget.configview.WidgetConfigLogic
import com.example.david.lists.widget.configview.WidgetConfigViewModel
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Named

@Module
class WidgetConfigViewModule {
    companion object {
        private const val WIDGET_ID = "widget_id"
    }

    @ViewScope
    @Provides
    fun logic(view: IWidgetConfigContract.View,
              viewModel: IWidgetConfigContract.ViewModel,
              @Named(WIDGET_ID) widgetId: Int,
              repo: IRepositoryContract.Repository,
              schedulerProvider: ISchedulerProviderContract,
              disposable: CompositeDisposable): IWidgetConfigContract.Logic {
        return WidgetConfigLogic(view, viewModel, widgetId, repo, schedulerProvider, disposable)
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): IWidgetConfigContract.ViewModel {
        return WidgetConfigViewModel(application)
    }

    @ViewScope
    @Provides
    fun adapter(logic: IWidgetConfigContract.Logic): IWidgetConfigContract.Adapter {
        return WidgetConfigAdapter(logic)
    }

    @ViewScope
    @Provides
    @Named(WIDGET_ID)
    fun widgetId(intent: Intent): Int {
        return intent.extras?.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        )!!
    }
}

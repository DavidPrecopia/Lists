package com.example.david.lists.widget.configview.buildlogic

import android.app.Application
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

@Module
class WidgetConfigViewModule {
    @ViewScope
    @Provides
    fun logic(view: IWidgetConfigContract.View,
              viewModel: IWidgetConfigContract.ViewModel,
              repo: IRepositoryContract.Repository,
              schedulerProvider: ISchedulerProviderContract,
              disposable: CompositeDisposable): IWidgetConfigContract.Logic {
        return WidgetConfigLogic(view, viewModel, repo, schedulerProvider, disposable)
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
}

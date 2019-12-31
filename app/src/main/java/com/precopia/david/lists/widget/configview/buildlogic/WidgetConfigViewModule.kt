package com.precopia.david.lists.widget.configview.buildlogic

import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.widget.common.UtilWidgetKeys
import com.precopia.david.lists.widget.configview.IWidgetConfigContract
import com.precopia.david.lists.widget.configview.WidgetConfigAdapter
import com.precopia.david.lists.widget.configview.WidgetConfigLogic
import com.precopia.david.lists.widget.configview.WidgetConfigViewModel
import com.precopia.domain.repository.IRepositoryContract
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

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(utilWidgetKeys: UtilWidgetKeys,
                  getStringRes: (Int) -> String): IWidgetConfigContract.ViewModel {
        return WidgetConfigViewModel(utilWidgetKeys, getStringRes)
    }

    @ViewScope
    @Provides
    fun adapter(logic: IWidgetConfigContract.Logic): IWidgetConfigContract.Adapter {
        return WidgetConfigAdapter(logic)
    }
}

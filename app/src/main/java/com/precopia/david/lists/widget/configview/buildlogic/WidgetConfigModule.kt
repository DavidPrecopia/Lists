package com.precopia.david.lists.widget.configview.buildlogic

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
import io.reactivex.rxjava3.disposables.CompositeDisposable

@Module
class WidgetConfigModule {
    @ViewScope
    @Provides
    fun logic(view: AppCompatActivity,
              factory: ViewModelProvider.NewInstanceFactory
    ): IWidgetConfigContract.Logic {
        return ViewModelProvider(view, factory).get(WidgetConfigLogic::class.java)
    }

    @ViewScope
    @Provides
    fun factory(viewModel: IWidgetConfigContract.ViewModel,
                repo: IRepositoryContract.Repository,
                schedulerProvider: ISchedulerProviderContract,
                disposable: CompositeDisposable
    ): ViewModelProvider.NewInstanceFactory {
        return WidgetConfigLogicFactory(viewModel, repo, schedulerProvider, disposable)
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

package com.precopia.david.lists.widget.configview.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.widget.configview.IWidgetConfigContract
import com.precopia.david.lists.widget.configview.WidgetConfigLogic
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable

class WidgetConfigLogicFactory(
        private val viewModel: IWidgetConfigContract.ViewModel,
        private val repo: IRepositoryContract.Repository,
        private val schedulerProvider: ISchedulerProviderContract,
        private val disposable: CompositeDisposable
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WidgetConfigLogic(viewModel, repo, schedulerProvider, disposable) as T
    }
}
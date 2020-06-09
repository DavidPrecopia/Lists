package com.precopia.david.lists.view.itemlist.buldlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.itemlist.IItemViewContract
import com.precopia.david.lists.view.itemlist.ItemLogic
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable

class ItemLogicFactory(
        private val viewModel: IItemViewContract.ViewModel,
        private val repo: IRepositoryContract.Repository,
        private val schedulerProvider: ISchedulerProviderContract,
        private val disposable: CompositeDisposable
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ItemLogic(
                viewModel, repo, schedulerProvider, disposable
        ) as T
    }
}
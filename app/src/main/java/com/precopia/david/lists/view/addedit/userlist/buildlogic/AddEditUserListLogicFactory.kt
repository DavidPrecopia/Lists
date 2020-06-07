package com.precopia.david.lists.view.addedit.userlist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.userlist.AddEditUserListLogic
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable

class AddEditUserListLogicFactory(
        private val viewModel: IAddEditContract.ViewModel,
        private val repo: IRepositoryContract.Repository,
        private val disposable: CompositeDisposable,
        private val schedulerProvider: ISchedulerProviderContract,
        private val id: String,
        private val title: String,
        private val position: Int
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEditUserListLogic(
                viewModel, repo, disposable, schedulerProvider, id, title, position
        ) as T
    }
}
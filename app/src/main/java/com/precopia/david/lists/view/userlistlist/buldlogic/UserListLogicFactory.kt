package com.precopia.david.lists.view.userlistlist.buldlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.IUtilNightModeContract
import com.precopia.david.lists.view.userlistlist.IUserListViewContract
import com.precopia.david.lists.view.userlistlist.UserListLogic
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable

class UserListLogicFactory(
        private val viewModel: IUserListViewContract.ViewModel,
        private val utilNightMode: IUtilNightModeContract,
        private val repo: IRepositoryContract.Repository,
        private val schedulerProvider: ISchedulerProviderContract,
        private val disposable: CompositeDisposable
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserListLogic(
                viewModel, utilNightMode, repo, schedulerProvider, disposable
        ) as T
    }
}
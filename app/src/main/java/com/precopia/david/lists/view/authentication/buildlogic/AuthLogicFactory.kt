package com.precopia.david.lists.view.authentication.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.authentication.AuthLogic
import com.precopia.david.lists.view.authentication.IAuthContract
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable

class AuthLogicFactory(
        private val viewModel: IAuthContract.ViewModel,
        private val userRepo: IRepositoryContract.UserRepository,
        private val disposable: CompositeDisposable,
        private val schedulerProvider: ISchedulerProviderContract
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthLogic(viewModel, userRepo, disposable, schedulerProvider) as T
    }
}
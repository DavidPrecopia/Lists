package com.precopia.david.lists.view.reauthentication.google.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.google.GoogleReAuthLogic
import com.precopia.david.lists.view.reauthentication.google.IGoogleReAuthContract
import com.precopia.domain.repository.IRepositoryContract

class GoogleReAuthLogicFactory(
        private val viewModel: IGoogleReAuthContract.ViewModel,
        private val userRepo: IRepositoryContract.UserRepository,
        private val schedulerProvider: ISchedulerProviderContract
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GoogleReAuthLogic(viewModel, userRepo, schedulerProvider) as T
    }
}
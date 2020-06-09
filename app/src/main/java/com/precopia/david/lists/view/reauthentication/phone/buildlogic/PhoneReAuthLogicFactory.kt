package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract
import com.precopia.david.lists.view.reauthentication.phone.PhoneReAuthLogic
import com.precopia.domain.repository.IRepositoryContract

class PhoneReAuthLogicFactory(
        private val viewModel: IPhoneReAuthContract.ViewModel,
        private val userRepo: IRepositoryContract.UserRepository,
        private val schedulerProvider: ISchedulerProviderContract
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PhoneReAuthLogic(viewModel, userRepo, schedulerProvider) as T
    }
}
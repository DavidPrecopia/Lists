package com.precopia.david.lists.view.reauthentication.email.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.email.EmailReAuthLogic
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract
import com.precopia.domain.repository.IRepositoryContract

class EmailReAuthLogicFactory(
        private val viewModel: IEmailReAuthContract.ViewModel,
        private val userRepo: IRepositoryContract.UserRepository,
        private val schedulerProvider: ISchedulerProviderContract
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EmailReAuthLogic(viewModel, userRepo, schedulerProvider) as T
    }
}
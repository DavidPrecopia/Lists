package com.precopia.david.lists.view.addedit.item.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.item.AddEditItemLogic
import com.precopia.domain.repository.IRepositoryContract

class AddEditItemLogicFactory(
        private val viewModel: IAddEditContract.ViewModel,
        private val repo: IRepositoryContract.Repository,
        private val schedulerProvider: ISchedulerProviderContract,
        private val id: String,
        private val title: String,
        private val userListId: String,
        private val position: Int
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEditItemLogic(
                viewModel, repo, schedulerProvider, id, title, userListId, position
        ) as T
    }
}
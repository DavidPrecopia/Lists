package com.example.david.lists.view.addedit.item.buildlogic

import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.ID
import com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.POSITION
import com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.TITLE
import com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.USER_LIST_ID
import com.example.david.lists.view.addedit.item.AddEditItemLogic
import com.example.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
internal class

AddEditItemDialogModule {
    @ViewScope
    @Provides
    fun logic(view: IAddEditContract.View,
              viewModel: IAddEditContract.ViewModel,
              repository: IRepositoryContract.Repository,
              schedulerProvider: ISchedulerProviderContract,
              @Named(ID) id: String,
              @Named(TITLE) title: String,
              @Named(USER_LIST_ID) userListId: String,
              @Named(POSITION) position: Int): IAddEditContract.Logic {
        return AddEditItemLogic(view, viewModel, repository, schedulerProvider,  id, title, userListId, position)
    }
}

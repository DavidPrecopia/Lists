package com.precopia.david.lists.view.addedit.item.buildlogic

import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.ID
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.POSITION
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.TITLE
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.USER_LIST_ID
import com.precopia.david.lists.view.addedit.item.AddEditItemLogic
import com.precopia.domain.repository.IRepositoryContract
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

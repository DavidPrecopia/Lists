package com.precopia.david.lists.view.addedit.userlist.buildlogic

import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.ID
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.POSITION
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.TITLE
import com.precopia.david.lists.view.addedit.userlist.AddEditUserListLogic
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
internal class AddEditUserListDialogModule {
    @ViewScope
    @Provides
    fun logic(view: IAddEditContract.View,
              viewModel: IAddEditContract.ViewModel,
              repository: IRepositoryContract.Repository,
              schedulerProvider: ISchedulerProviderContract,
              @Named(ID) id: String,
              @Named(TITLE) title: String,
              @Named(POSITION) position: Int): IAddEditContract.Logic {
        return AddEditUserListLogic(view, viewModel, repository, schedulerProvider, id, title, position)
    }
}

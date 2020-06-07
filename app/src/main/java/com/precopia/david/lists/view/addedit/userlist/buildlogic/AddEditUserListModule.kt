package com.precopia.david.lists.view.addedit.userlist.buildlogic

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
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
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Named

@Module
internal class AddEditUserListModule {
    @ViewScope
    @Provides
    fun logic(view: DialogFragment,
              factory: ViewModelProvider.NewInstanceFactory): IAddEditContract.Logic {
        return ViewModelProvider(view, factory).get(AddEditUserListLogic::class.java)
    }

    @ViewScope
    @Provides
    fun factory(viewModel: IAddEditContract.ViewModel,
                repository: IRepositoryContract.Repository,
                disposable: CompositeDisposable,
                schedulerProvider: ISchedulerProviderContract,
                @Named(ID) id: String,
                @Named(TITLE) title: String,
                @Named(POSITION) position: Int): ViewModelProvider.NewInstanceFactory {
        return AddEditUserListLogicFactory(
                viewModel, repository, disposable, schedulerProvider, id, title, position
        )
    }
}

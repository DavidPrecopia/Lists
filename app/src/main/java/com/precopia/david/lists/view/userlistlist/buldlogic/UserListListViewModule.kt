package com.precopia.david.lists.view.userlistlist.buldlogic

import androidx.recyclerview.widget.ItemTouchHelper
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.IUtilNightModeContract
import com.precopia.david.lists.view.userlistlist.IUserListViewContract
import com.precopia.david.lists.view.userlistlist.UserListAdapter
import com.precopia.david.lists.view.userlistlist.UserListListLogic
import com.precopia.david.lists.view.userlistlist.UserListListViewModel
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable

@Module
internal class UserListListViewModule {
    @ViewScope
    @Provides
    fun logic(view: IUserListViewContract.View,
              viewModel: IUserListViewContract.ViewModel,
              utilNightMode: IUtilNightModeContract,
              repo: IRepositoryContract.Repository,
              schedulerProvider: ISchedulerProviderContract,
              disposable: CompositeDisposable): IUserListViewContract.Logic {
        return UserListListLogic(view, viewModel, utilNightMode, repo, schedulerProvider, disposable)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IUserListViewContract.ViewModel {
        return UserListListViewModel(getStringRes)
    }

    @ViewScope
    @Provides
    fun adapter(logic: IUserListViewContract.Logic,
                viewBinderHelper: ViewBinderHelper,
                itemTouchHelper: ItemTouchHelper): IUserListViewContract.Adapter {
        return UserListAdapter(logic, viewBinderHelper, itemTouchHelper)
    }
}

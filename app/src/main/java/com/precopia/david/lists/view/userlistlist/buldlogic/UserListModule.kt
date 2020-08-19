package com.precopia.david.lists.view.userlistlist.buldlogic

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.userlistlist.IUserListViewContract
import com.precopia.david.lists.view.userlistlist.UserListAdapter
import com.precopia.david.lists.view.userlistlist.UserListLogic
import com.precopia.david.lists.view.userlistlist.UserListViewModel
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable

@Module
internal class UserListModule {
    @ViewScope
    @Provides
    fun logic(view: Fragment,
              factory: ViewModelProvider.NewInstanceFactory): IUserListViewContract.Logic {
        return ViewModelProvider(view, factory).get(UserListLogic::class.java)
    }

    @ViewScope
    @Provides
    fun factory(viewModel: IUserListViewContract.ViewModel,
                repo: IRepositoryContract.Repository,
                schedulerProvider: ISchedulerProviderContract,
                disposable: CompositeDisposable): ViewModelProvider.NewInstanceFactory {
        return UserListLogicFactory(viewModel, repo, schedulerProvider, disposable)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IUserListViewContract.ViewModel {
        return UserListViewModel(getStringRes)
    }

    @ViewScope
    @Provides
    fun adapter(logic: IUserListViewContract.Logic,
                viewBinderHelper: ViewBinderHelper,
                itemTouchHelper: ItemTouchHelper): IUserListViewContract.Adapter {
        return UserListAdapter(logic, viewBinderHelper, itemTouchHelper)
    }
}

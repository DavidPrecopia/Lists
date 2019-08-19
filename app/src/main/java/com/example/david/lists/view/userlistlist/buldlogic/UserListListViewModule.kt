package com.example.david.lists.view.userlistlist.buldlogic

import android.app.Application

import androidx.recyclerview.widget.ItemTouchHelper

import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.IUtilNightModeContract
import com.example.david.lists.util.UtilNightMode
import com.example.david.lists.view.userlistlist.IUserListViewContract
import com.example.david.lists.view.userlistlist.UserListAdapter
import com.example.david.lists.view.userlistlist.UserListListLogic
import com.example.david.lists.view.userlistlist.UserListListViewModel

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
internal class UserListListViewModule {
    @ViewScope
    @Provides
    fun logic(view: IUserListViewContract.View,
              viewModel: IUserListViewContract.ViewModel,
              userRepo: IRepositoryContract.UserRepository,
              utilNightMode: IUtilNightModeContract,
              repo: IRepositoryContract.Repository,
              schedulerProvider: ISchedulerProviderContract,
              disposable: CompositeDisposable): IUserListViewContract.Logic {
        return UserListListLogic(view, viewModel, userRepo, utilNightMode, repo, schedulerProvider, disposable)
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): IUserListViewContract.ViewModel {
        return UserListListViewModel(application, 100)
    }

    @ViewScope
    @Provides
    fun adapter(logic: IUserListViewContract.Logic,
                viewBinderHelper: ViewBinderHelper,
                itemTouchHelper: ItemTouchHelper): IUserListViewContract.Adapter {
        return UserListAdapter(logic, viewBinderHelper, itemTouchHelper)
    }

    @ViewScope
    @Provides
    fun utilNightMode(application: Application): IUtilNightModeContract {
        return UtilNightMode(application)
    }
}
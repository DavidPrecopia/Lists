package com.example.david.lists.view.userlistlist.buldlogic;

import android.app.Application;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.view.userlistlist.IUserListViewContract;
import com.example.david.lists.view.userlistlist.UserListAdapter;
import com.example.david.lists.view.userlistlist.UserListLogic;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class UserListListViewModule {
    @ViewScope
    @Provides
    IUserListViewContract.Logic viewModel(Application application,
                                          IUserListViewContract.View view,
                                          IUserListViewContract.Adapter adapter,
                                          IRepositoryContract.Repository repo,
                                          IRepositoryContract.UserRepository userRepo,
                                          ISchedulerProviderContract schedulerProvider,
                                          CompositeDisposable disposable) {
        return new UserListLogic(application, view, adapter, repo, userRepo, schedulerProvider, disposable);
    }

    @ViewScope
    @Provides
    IUserListViewContract.Adapter adapter(ViewBinderHelper viewBinderHelper,
                                          ItemTouchHelper itemTouchHelper) {
        return new UserListAdapter(viewBinderHelper, itemTouchHelper);
    }
}

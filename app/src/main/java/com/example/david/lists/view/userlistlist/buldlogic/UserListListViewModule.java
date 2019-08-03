package com.example.david.lists.view.userlistlist.buldlogic;

import android.app.Application;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.view.userlistlist.IUserListViewContract;
import com.example.david.lists.view.userlistlist.UserListAdapter;
import com.example.david.lists.view.userlistlist.UserListListLogic;
import com.example.david.lists.view.userlistlist.UserListListViewModel;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class UserListListViewModule {
    @ViewScope
    @Provides
    IUserListViewContract.Logic logic(Application application,
                                      IUserListViewContract.View view,
                                      IUserListViewContract.ViewModel viewModel,
                                      IRepositoryContract.Repository repo,
                                      IRepositoryContract.UserRepository userRepo,
                                      ISchedulerProviderContract schedulerProvider,
                                      CompositeDisposable disposable) {
        return new UserListListLogic(application, view, viewModel, repo, userRepo, schedulerProvider, disposable);
    }

    @ViewScope
    @Provides
    IUserListViewContract.ViewModel viewModel(Application application) {
        return new UserListListViewModel(application, 100);
    }

    @ViewScope
    @Provides
    IUserListViewContract.Adapter adapter(IUserListViewContract.Logic logic,
                                          ViewBinderHelper viewBinderHelper,
                                          ItemTouchHelper itemTouchHelper) {
        return new UserListAdapter(logic, viewBinderHelper, itemTouchHelper);
    }
}

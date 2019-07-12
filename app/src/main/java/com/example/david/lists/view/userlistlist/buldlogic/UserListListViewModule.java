package com.example.david.lists.view.userlistlist.buldlogic;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.common.ViewModelFactory;
import com.example.david.lists.view.userlistlist.IUserListViewContract;
import com.example.david.lists.view.userlistlist.UserListAdapter;
import com.example.david.lists.view.userlistlist.UserListViewModel;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class UserListListViewModule {
    @ViewScope
    @Provides
    IUserListViewContract.ViewModel viewModel(Fragment fragment, ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(fragment, factory).get(UserListViewModel.class);
    }

    @ViewScope
    @Provides
    ViewModelProvider.Factory viewModelFactory(Application application,
                                               IRepositoryContract.Repository repository,
                                               IRepositoryContract.UserRepository userRepository,
                                               CompositeDisposable disposable,
                                               SharedPreferences sharedPrefs) {
        return new ViewModelFactory(application, repository, userRepository, disposable, sharedPrefs);
    }

    @ViewScope
    @Provides
    IUserListViewContract.Adapter userListAdapter(IUserListViewContract.ViewModel viewModel,
                                                  ViewBinderHelper viewBinderHelper,
                                                  ItemTouchHelper itemTouchHelper) {
        return new UserListAdapter(viewModel, viewBinderHelper, itemTouchHelper);
    }
}

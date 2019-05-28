package com.example.david.lists.di.view.userlistfragment;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.data.repository.IUserRepository;
import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.common.ViewModelFactory;
import com.example.david.lists.view.userlistlist.IUserListAdapter;
import com.example.david.lists.view.userlistlist.IUserListViewModel;
import com.example.david.lists.view.userlistlist.UserListAdapterImpl;
import com.example.david.lists.view.userlistlist.UserListViewModelImpl;

import dagger.Module;
import dagger.Provides;

@Module
final class UserListFragmentModule {
    @ViewScope
    @Provides
    IUserListViewModel viewModel(Fragment fragment, ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(fragment, factory).get(UserListViewModelImpl.class);
    }

    @ViewScope
    @Provides
    ViewModelProvider.Factory viewModelFactory(Application application, IRepository repository, IUserRepository userRepository, SharedPreferences sharedPrefs) {
        return new ViewModelFactory(application, repository, userRepository, sharedPrefs);
    }

    @ViewScope
    @Provides
    IUserListAdapter userListAdapter(IUserListViewModel viewModel, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        return new UserListAdapterImpl(viewModel, viewBinderHelper, itemTouchHelper);
    }
}

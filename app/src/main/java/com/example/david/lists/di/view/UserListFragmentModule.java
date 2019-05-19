package com.example.david.lists.di.view;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.ui.adapaters.IUserListAdapterContract;
import com.example.david.lists.ui.adapaters.UserListsAdapter;
import com.example.david.lists.ui.viewmodels.IUserListViewModel;
import com.example.david.lists.ui.viewmodels.UserListViewModelFactory;
import com.example.david.lists.ui.viewmodels.UserListViewModelImpl;

import dagger.Module;
import dagger.Provides;

@Module
class UserListFragmentModule {
    @UserListFragmentScope
    @Provides
    IUserListViewModel viewModel(Fragment fragment, UserListViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(UserListViewModelImpl.class);
    }

    @UserListFragmentScope
    @Provides
    UserListViewModelFactory viewModelFactory(Application application, IRepository repository, SharedPreferences sharedPrefs) {
        return new UserListViewModelFactory(application, repository, sharedPrefs);
    }

    @UserListFragmentScope
    @Provides
    IRepository repository(Application application) {
        return ((MyApplication) application).getAppComponent().repository();
    }

    @UserListFragmentScope
    @Provides
    SharedPreferences sharedPreferences(Application application) {
        return ((MyApplication) application).getAppComponent().sharedPrefsNightMode();
    }

    @UserListFragmentScope
    @Provides
    IUserListAdapterContract userListAdapter(IUserListViewModel viewModel, ItemTouchHelper itemTouchHelper) {
        return new UserListsAdapter(viewModel, itemTouchHelper);
    }
}

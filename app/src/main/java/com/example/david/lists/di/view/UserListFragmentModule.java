package com.example.david.lists.di.view;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.adapaters.IUserListAdapterContract;
import com.example.david.lists.ui.adapaters.TouchHelperCallback;
import com.example.david.lists.ui.adapaters.UserListsAdapter;
import com.example.david.lists.ui.view.MainActivity;
import com.example.david.lists.ui.view.UserListsFragment;
import com.example.david.lists.ui.viewmodels.IUserListViewModelContract;
import com.example.david.lists.ui.viewmodels.UserListViewModel;
import com.example.david.lists.ui.viewmodels.UserListViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
class UserListFragmentModule {
    @UserListFragmentScope
    @Provides
    IUserListViewModelContract viewModel(Fragment fragment, UserListViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(UserListViewModel.class);
    }

    @UserListFragmentScope
    @Provides
    UserListViewModelFactory viewModelFactory(Application application, IModelContract model, SharedPreferences sharedPrefs) {
        return new UserListViewModelFactory(application, model, sharedPrefs);
    }

    @UserListFragmentScope
    @Provides
    IModelContract model(Application application) {
        return ((MyApplication) application).getAppComponent().getModel();
    }

    @UserListFragmentScope
    @Provides
    SharedPreferences sharedPreferences(Application application) {
        return ((MyApplication) application).getAppComponent().getSharedPrefsNightMode();
    }

    @UserListFragmentScope
    @Provides
    IUserListAdapterContract userListAdapter(IUserListViewModelContract viewModel, ItemTouchHelper itemTouchHelper) {
        return new UserListsAdapter(viewModel, itemTouchHelper);
    }
}

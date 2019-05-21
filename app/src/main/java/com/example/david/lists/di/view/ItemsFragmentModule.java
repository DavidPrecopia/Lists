package com.example.david.lists.di.view;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.ui.itemlist.IItemAdapterContract;
import com.example.david.lists.ui.itemlist.IItemViewModel;
import com.example.david.lists.ui.itemlist.ItemViewModelFactory;
import com.example.david.lists.ui.itemlist.ItemViewModelImpl;
import com.example.david.lists.ui.itemlist.ItemsAdapter;

import dagger.Module;
import dagger.Provides;

@Module
final class ItemsFragmentModule {
    @ItemsFragmentScope
    @Provides
    IItemViewModel viewModel(Fragment fragment, ItemViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(ItemViewModelImpl.class);
    }

    @ItemsFragmentScope
    @Provides
    ItemViewModelFactory viewModelFactory(Application application, IRepository repository, String userListId) {
        return new ItemViewModelFactory(application, repository, userListId);
    }

    @ItemsFragmentScope
    @Provides
    IRepository repository(Application application) {
        return ((MyApplication) application).getAppComponent().repository();
    }

    @ItemsFragmentScope
    @Provides
    IItemAdapterContract adapter(IItemViewModel viewModel, ItemTouchHelper itemTouchHelper) {
        return new ItemsAdapter(viewModel, itemTouchHelper);
    }
}

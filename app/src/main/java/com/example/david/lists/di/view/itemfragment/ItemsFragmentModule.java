package com.example.david.lists.di.view.itemfragment;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.common.ViewModelFactory;
import com.example.david.lists.view.itemlist.IItemAdapter;
import com.example.david.lists.view.itemlist.IItemViewModel;
import com.example.david.lists.view.itemlist.ItemViewModelImpl;
import com.example.david.lists.view.itemlist.ItemsAdapterImpl;

import dagger.Module;
import dagger.Provides;

@Module
final class ItemsFragmentModule {
    @ViewScope
    @Provides
    IItemViewModel viewModel(Fragment fragment, ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(fragment, factory).get(ItemViewModelImpl.class);
    }

    @ViewScope
    @Provides
    ViewModelProvider.Factory viewModelFactory(Application application, IRepository repository, String userListId) {
        return new ViewModelFactory(application, repository, userListId);
    }

    @ViewScope
    @Provides
    IItemAdapter adapter(IItemViewModel viewModel, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        return new ItemsAdapterImpl(viewModel, viewBinderHelper, itemTouchHelper);
    }

    @ViewScope
    @Provides
    ViewBinderHelper viewBinderHelper() {
        ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
        viewBinderHelper.setOpenOnlyOne(true);
        return viewBinderHelper;
    }
}

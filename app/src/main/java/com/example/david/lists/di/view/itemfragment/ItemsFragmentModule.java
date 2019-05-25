package com.example.david.lists.di.view.itemfragment;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.di.view.ViewScope;
import com.example.david.lists.ui.itemlist.IItemAdapter;
import com.example.david.lists.ui.itemlist.IItemViewModel;
import com.example.david.lists.ui.itemlist.ItemViewModelFactory;
import com.example.david.lists.ui.itemlist.ItemViewModelImpl;
import com.example.david.lists.ui.itemlist.ItemsAdapterImpl;

import dagger.Module;
import dagger.Provides;

@Module
final class ItemsFragmentModule {
    @ViewScope
    @Provides
    IItemViewModel viewModel(Fragment fragment, ItemViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(ItemViewModelImpl.class);
    }

    @ViewScope
    @Provides
    ItemViewModelFactory viewModelFactory(Application application, IRepository repository, String userListId) {
        return new ItemViewModelFactory(application, repository, userListId);
    }

    @ViewScope
    @Provides
    IRepository repository(Application application) {
        return ((MyApplication) application).getAppComponent().repository();
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

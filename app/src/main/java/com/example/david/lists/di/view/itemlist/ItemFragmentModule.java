package com.example.david.lists.di.view.itemlist;

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
import com.example.david.lists.view.itemlist.ItemAdapterImpl;
import com.example.david.lists.view.itemlist.ItemViewModelImpl;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class ItemFragmentModule {
    @ViewScope
    @Provides
    IItemViewModel viewModel(Fragment fragment, ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(fragment, factory).get(ItemViewModelImpl.class);
    }

    @ViewScope
    @Provides
    ViewModelProvider.Factory viewModelFactory(Application application, IRepository repository, CompositeDisposable disposable, String userListId) {
        return new ViewModelFactory(application, repository, disposable, userListId);
    }

    @ViewScope
    @Provides
    IItemAdapter adapter(IItemViewModel viewModel, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        return new ItemAdapterImpl(viewModel, viewBinderHelper, itemTouchHelper);
    }
}

package com.example.david.lists.view.itemlist.buldlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.common.ViewModelFactory;
import com.example.david.lists.view.itemlist.IItemViewContract;
import com.example.david.lists.view.itemlist.ItemAdapter;
import com.example.david.lists.view.itemlist.ItemViewModel;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class ItemListViewModule {
    @ViewScope
    @Provides
    IItemViewContract.ViewModel viewModel(Fragment fragment, ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(fragment, factory).get(ItemViewModel.class);
    }

    @ViewScope
    @Provides
    ViewModelProvider.Factory viewModelFactory(Application application,
                                               IRepositoryContract.Repository repository,
                                               CompositeDisposable disposable,
                                               String userListId) {
        return new ViewModelFactory(application, repository, disposable, userListId);
    }

    @ViewScope
    @Provides
    IItemViewContract.Adapter adapter(IItemViewContract.ViewModel viewModel, ViewBinderHelper viewBinderHelper, ItemTouchHelper itemTouchHelper) {
        return new ItemAdapter(viewModel, viewBinderHelper, itemTouchHelper);
    }
}

package com.example.david.lists.view.itemlist.buldlogic;

import android.app.Application;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.itemlist.IItemViewContract;
import com.example.david.lists.view.itemlist.ItemAdapter;
import com.example.david.lists.view.itemlist.ItemLogic;
import com.example.david.lists.view.itemlist.ItemViewModel;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class ItemListViewModule {
    @ViewScope
    @Provides
    IItemViewContract.Logic logic(IItemViewContract.View view,
                                  IItemViewContract.ViewModel viewModel,
                                  IItemViewContract.Adapter adapter,
                                  IRepositoryContract.Repository repository,
                                  CompositeDisposable disposable) {
        return new ItemLogic(view, viewModel, adapter, repository, disposable);
    }

    @ViewScope
    @Provides
    IItemViewContract.ViewModel viewModel(Application application, String userListId) {
        return new ItemViewModel(application, userListId);
    }

    @ViewScope
    @Provides
    IItemViewContract.Adapter adapter(ViewBinderHelper viewBinderHelper,
                                      ItemTouchHelper itemTouchHelper) {
        return new ItemAdapter(viewBinderHelper, itemTouchHelper);
    }
}

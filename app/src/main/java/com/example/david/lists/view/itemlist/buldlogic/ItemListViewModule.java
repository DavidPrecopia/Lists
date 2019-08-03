package com.example.david.lists.view.itemlist.buldlogic;

import android.app.Application;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.view.itemlist.IItemViewContract;
import com.example.david.lists.view.itemlist.ItemAdapter;
import com.example.david.lists.view.itemlist.ItemListLogic;
import com.example.david.lists.view.itemlist.ItemListViewModel;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class ItemListViewModule {
    @ViewScope
    @Provides
    IItemViewContract.Logic logic(IItemViewContract.View view,
                                  IItemViewContract.ViewModel viewModel,
                                  IRepositoryContract.Repository repo,
                                  ISchedulerProviderContract schedulerProvider,
                                  CompositeDisposable disposable) {
        return new ItemListLogic(view, viewModel, repo, schedulerProvider, disposable);
    }

    @ViewScope
    @Provides
    IItemViewContract.ViewModel viewModel(Application application, String userListId) {
        return new ItemListViewModel(application, userListId);
    }

    @ViewScope
    @Provides
    IItemViewContract.Adapter adapter(IItemViewContract.Logic logic,
                                      ViewBinderHelper viewBinderHelper,
                                      ItemTouchHelper itemTouchHelper) {
        return new ItemAdapter(logic, viewBinderHelper, itemTouchHelper);
    }
}

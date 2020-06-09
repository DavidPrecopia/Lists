package com.precopia.david.lists.view.itemlist.buldlogic

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.itemlist.IItemViewContract
import com.precopia.david.lists.view.itemlist.ItemAdapter
import com.precopia.david.lists.view.itemlist.ItemLogic
import com.precopia.david.lists.view.itemlist.ItemViewModel
import com.precopia.domain.repository.IRepositoryContract
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable

@Module
internal class ItemModule {
    @ViewScope
    @Provides
    fun logic(view: Fragment,
              factory: ViewModelProvider.NewInstanceFactory): IItemViewContract.Logic {
        return ViewModelProvider(view, factory).get(ItemLogic::class.java)
    }

    @ViewScope
    @Provides
    fun factory(viewModel: IItemViewContract.ViewModel,
                repo: IRepositoryContract.Repository,
                schedulerProvider: ISchedulerProviderContract,
                disposable: CompositeDisposable): ViewModelProvider.NewInstanceFactory {
        return ItemLogicFactory(viewModel, repo, schedulerProvider, disposable)
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String,
                  getStringResArg: (Int, String) -> String,
                  userListId: String): IItemViewContract.ViewModel {
        return ItemViewModel(getStringRes, getStringResArg, userListId)
    }

    @ViewScope
    @Provides
    fun adapter(logic: IItemViewContract.Logic,
                viewBinderHelper: ViewBinderHelper,
                itemTouchHelper: ItemTouchHelper): IItemViewContract.Adapter {
        return ItemAdapter(logic, viewBinderHelper, itemTouchHelper)
    }
}

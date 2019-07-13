package com.example.david.lists.view.addedit.item.buildlogic;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.view.addedit.common.IAddEditContract;
import com.example.david.lists.view.addedit.item.AddEditItemLogic;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

import static com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.ID;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.TITLE;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.USER_LIST_ID;

@Module
final class AddEditItemDialogModule {
    @ViewScope
    @Provides
    IAddEditContract.Logic logic(IAddEditContract.View view,
                                 IAddEditContract.ViewModel viewModel,
                                 IRepositoryContract.Repository repository,
                                 ISchedulerProviderContract schedulerProvider,
                                 CompositeDisposable disposable,
                                 @Named(ID) String id,
                                 @Named(TITLE) String title,
                                 @Named(USER_LIST_ID) String userListId) {
        return new AddEditItemLogic(view, viewModel, repository, schedulerProvider, disposable, id, title, userListId);
    }
}

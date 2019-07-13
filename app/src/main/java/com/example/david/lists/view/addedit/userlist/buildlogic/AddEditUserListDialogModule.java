package com.example.david.lists.view.addedit.userlist.buildlogic;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.view.addedit.common.IAddEditContract;
import com.example.david.lists.view.addedit.userlist.AddEditUserListLogic;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

import static com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.ID;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.TITLE;

@Module
final class AddEditUserListDialogModule {
    @ViewScope
    @Provides
    IAddEditContract.Logic logic(IAddEditContract.View view,
                                 IAddEditContract.ViewModel viewModel,
                                 IRepositoryContract.Repository repository,
                                 ISchedulerProviderContract schedulerProvider,
                                 CompositeDisposable disposable,
                                 @Named(ID) String id,
                                 @Named(TITLE) String title) {
        return new AddEditUserListLogic(view, viewModel, repository, schedulerProvider, disposable, id, title);
    }
}

package com.example.david.lists.view.addedit.userlist.buildlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.view.addedit.common.AddEditViewModelBase;
import com.example.david.lists.view.addedit.common.AddEditViewModelFactory;
import com.example.david.lists.view.addedit.userlist.AddEditUserListViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.ID;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.TITLE;

@Module
final class AddEditUserListDialogModule {
    @ViewScope
    @Provides
    AddEditViewModelBase viewModel(Fragment fragment, AddEditViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(AddEditUserListViewModel.class);
    }

    @ViewScope
    @Provides
    AddEditViewModelFactory factory(Application application,
                                    IRepository repository,
                                    CompositeDisposable disposable,
                                    @Named(ID) String id,
                                    @Named(TITLE) String title) {
        return new AddEditViewModelFactory(application, repository, disposable, id, title);
    }
}

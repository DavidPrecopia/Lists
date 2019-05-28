package com.example.david.lists.di.view.addedit.userlist;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.addedit.AddEditViewModelBase;
import com.example.david.lists.view.addedit.AddEditViewModelFactory;
import com.example.david.lists.view.addedit.userlist.AddEditUserListViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.example.david.lists.di.view.addedit.AddEditNamedConstants.ID;
import static com.example.david.lists.di.view.addedit.AddEditNamedConstants.TITLE;

@Module
final class AddEditUserListDialogFragmentModule {
    @ViewScope
    @Provides
    AddEditViewModelBase viewModel(Fragment fragment, AddEditViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(AddEditUserListViewModel.class);
    }

    @ViewScope
    @Provides
    AddEditViewModelFactory factory(Application application,
                                    IRepository repository,
                                    @Named(ID) String id,
                                    @Named(TITLE) String title) {
        return new AddEditViewModelFactory(application, repository, id, title);
    }
}

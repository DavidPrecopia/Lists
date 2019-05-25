package com.example.david.lists.di.view.addeditfragment;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.ui.addedit.AddEditViewModelBase;
import com.example.david.lists.ui.addedit.AddEditViewModelFactory;
import com.example.david.lists.ui.addedit.userlist.AddEditUserListViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.ID;
import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.TITLE;

@Module
final class AddEditUserListFragmentModule {
    @AddEditFragmentScope
    @Provides
    AddEditViewModelBase viewModel(Fragment fragment, AddEditViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(AddEditUserListViewModel.class);
    }

    @AddEditFragmentScope
    @Provides
    AddEditViewModelFactory factory(Application application,
                                    IRepository repository,
                                    @Named(ID) String id,
                                    @Named(TITLE) String title) {
        return new AddEditViewModelFactory(application, repository, id, title);
    }
}

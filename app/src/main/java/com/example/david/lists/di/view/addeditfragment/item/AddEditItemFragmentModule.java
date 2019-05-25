package com.example.david.lists.di.view.addeditfragment.item;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.di.view.ViewScope;
import com.example.david.lists.view.addedit.AddEditViewModelBase;
import com.example.david.lists.view.addedit.AddEditViewModelFactory;
import com.example.david.lists.view.addedit.item.AddEditItemViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.ID;
import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.TITLE;
import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.USER_LIST_ID;

@Module
final class AddEditItemFragmentModule {
    @ViewScope
    @Provides
    AddEditViewModelBase viewModel(Fragment fragment, AddEditViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(AddEditItemViewModel.class);
    }

    @ViewScope
    @Provides
    AddEditViewModelFactory factory(Application application,
                                    IRepository repository,
                                    @Named(ID) String id,
                                    @Named(TITLE) String title,
                                    @Named(USER_LIST_ID) String userListId) {
        return new AddEditViewModelFactory(application, repository, id, title, userListId);
    }
}

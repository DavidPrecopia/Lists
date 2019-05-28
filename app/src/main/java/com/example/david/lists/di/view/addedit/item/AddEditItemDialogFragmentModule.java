package com.example.david.lists.di.view.addedit.item;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.addedit.AddEditViewModelBase;
import com.example.david.lists.view.addedit.AddEditViewModelFactory;
import com.example.david.lists.view.addedit.item.AddEditItemViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.example.david.lists.di.view.addedit.AddEditNamedConstants.ID;
import static com.example.david.lists.di.view.addedit.AddEditNamedConstants.TITLE;
import static com.example.david.lists.di.view.addedit.AddEditNamedConstants.USER_LIST_ID;

@Module
final class AddEditItemDialogFragmentModule {
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

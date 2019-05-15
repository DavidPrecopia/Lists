package com.example.david.lists.di.view;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.adapaters.IItemAdapterContract;
import com.example.david.lists.ui.adapaters.ItemsAdapter;
import com.example.david.lists.ui.viewmodels.IItemViewModelContract;
import com.example.david.lists.ui.viewmodels.ItemViewModel;
import com.example.david.lists.ui.viewmodels.ItemViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
final class ItemsFragmentModule {
    @ItemsFragmentScope
    @Provides
    IItemViewModelContract viewModel(Fragment fragment, ItemViewModelFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(ItemViewModel.class);
    }

    @ItemsFragmentScope
    @Provides
    ItemViewModelFactory viewModelFactory(Application application, IModelContract model, String userListId) {
        return new ItemViewModelFactory(application, model, userListId);
    }

    @ItemsFragmentScope
    @Provides
    IModelContract model(Application application) {
        return ((MyApplication) application).getAppComponent().getModel();
    }

    @ItemsFragmentScope
    @Provides
    IItemAdapterContract adapter(IItemViewModelContract viewModel, ItemTouchHelper itemTouchHelper) {
        return new ItemsAdapter(viewModel, itemTouchHelper);
    }
}

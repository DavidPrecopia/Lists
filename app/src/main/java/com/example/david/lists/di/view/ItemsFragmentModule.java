package com.example.david.lists.di.view;

import android.app.Application;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.viewmodels.ItemViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
final class ItemsFragmentModule {
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
}

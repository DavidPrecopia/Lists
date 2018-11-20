package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.util.UtilViewModelFactory;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public final class UtilListViewModels {
    private UtilListViewModels() {
    }

    public static IViewModelContract getUserListViewModel(Fragment fragment, Application application) {
        UtilViewModelFactory factory = new UtilViewModelFactory(application);
        return ViewModelProviders.of(fragment, factory).get(UserListViewModel.class);
    }

    public static IViewModelContract getItemViewModel(Fragment fragment, Application application) {
        UtilViewModelFactory factory = new UtilViewModelFactory(application);
        return ViewModelProviders.of(fragment, factory).get(ItemViewModel.class);
    }
}
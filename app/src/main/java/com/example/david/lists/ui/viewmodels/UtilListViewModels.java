package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.util.UtilViewModelFactory;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public final class UtilListViewModels {
    private UtilListViewModels() {
    }

    public static IGroupViewModelContract getGroupViewModel(Fragment fragment, Application application) {
        UtilViewModelFactory factory = new UtilViewModelFactory(application, null);
        return ViewModelProviders.of(fragment, factory).get(GroupViewModel.class);
    }

    public static IItemViewModelContract getItemViewModel(Fragment fragment, Application application, String groupId) {
        UtilViewModelFactory factory = new UtilViewModelFactory(application, groupId);
        return ViewModelProviders.of(fragment, factory).get(ItemViewModel.class);
    }
}
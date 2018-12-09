package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public final class UtilListViewModels {
    private UtilListViewModels() {
    }

    public static IGroupViewModelContract getGroupViewModel(Fragment fragment, Application application) {
        GroupViewModelFactory factory = new GroupViewModelFactory(application);
        return ViewModelProviders.of(fragment, factory).get(GroupViewModel.class);
    }

    public static IItemViewModelContract getItemViewModel(Fragment fragment, Application application, String groupId) {
        ItemViewModelFactory factory = new ItemViewModelFactory(application, groupId);
        return ViewModelProviders.of(fragment, factory).get(ItemViewModel.class);
    }
}
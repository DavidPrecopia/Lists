package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.ui.view.ListActivity;
import com.example.david.lists.ui.view.ListFragment;
import com.example.david.lists.util.UtilViewModelFactory;

import androidx.lifecycle.ViewModelProviders;

public final class UtilListViewModels {
    private UtilListViewModels() {
    }

    public static IViewModelContract getUserListViewModel(ListActivity activity, Application application) {
        UtilViewModelFactory factory = new UtilViewModelFactory(application);
        return ViewModelProviders.of(activity, factory).get(UserListViewModel.class);
    }

    public static IViewModelContract getItemViewModel(ListFragment fragment, Application application) {
        UtilViewModelFactory factory = new UtilViewModelFactory(application);
        return ViewModelProviders.of(fragment, factory).get(ItemViewModel.class);
    }
}
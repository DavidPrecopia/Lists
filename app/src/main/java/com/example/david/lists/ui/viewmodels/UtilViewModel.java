package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.ui.view.ListActivity;
import com.example.david.lists.ui.view.ListFragment;

import androidx.lifecycle.ViewModelProviders;

public final class UtilViewModel {
    private UtilViewModel() {
    }

    public static IListViewModelContract getUserListViewModel(ListActivity activity, Application application) {
        ViewModelFactory factory = new ViewModelFactory(application);
        return ViewModelProviders.of(activity, factory).get(UserListViewModel.class);
    }

    public static IListViewModelContract getItemViewModel(ListFragment fragment, Application application) {
        ViewModelFactory factory = new ViewModelFactory(application);
        return ViewModelProviders.of(fragment, factory).get(ItemViewModel.class);
    }
}

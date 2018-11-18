package com.example.david.lists.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.david.lists.R;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.data.model.Model;
import com.example.david.lists.ui.viewmodels.ItemViewModel;
import com.example.david.lists.ui.viewmodels.UserListViewModel;
import com.example.david.lists.widget.configactivity.WidgetConfigViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public final class UtilViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final IModelContract model;

    public UtilViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
        this.model = Model.getInstance();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            //noinspection unchecked
            return (T) new UserListViewModel(application, model);
        } else if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application, model, getUserListId(), getListTitle());
        } else if (modelClass.isAssignableFrom(WidgetConfigViewModel.class)) {
            //noinspection unchecked
            return (T) new WidgetConfigViewModel(application, model);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }


    private String getUserListId() {
        return getSharedPreferences().getString(
                getStringResource(R.string.key_shared_pref_user_list_id),
                ""
        );
    }

    private String getListTitle() {
        return getSharedPreferences().getString(
                getStringResource(R.string.key_shared_pref_user_list_title),
                null
        );
    }

    private SharedPreferences getSharedPreferences() {
        return application.getSharedPreferences(
                getStringResource(R.string.key_shared_prefs_name),
                Context.MODE_PRIVATE
        );
    }

    private String getStringResource(int stringResId) {
        return application.getString(stringResId);
    }
}
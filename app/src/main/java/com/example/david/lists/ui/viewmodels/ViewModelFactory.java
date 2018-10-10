package com.example.david.lists.ui.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.david.lists.R;
import com.example.david.lists.model.IModelContract;
import com.example.david.lists.model.Model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

final class ViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final IModelContract model;

    ViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
        this.model = Model.getInstance(application);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            //noinspection unchecked
            return (T) new UserListViewModel(application, model);
        } else if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            //noinspection unchecked
            return (T) new ItemViewModel(application, model, getListId(), getListTitle());
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }


    private int getListId() {
        return getSharedPreferences().getInt(
                getStringResource(R.string.key_shared_pref_user_list_id),
                -1
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
package com.example.david.lists.di.view.addeditfragment;

import android.app.Application;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.util.UtilSoftKeyboard;

import dagger.Module;
import dagger.Provides;

@Module
final class AddEditFragmentCommonModule {
    @AddEditFragmentScope
    @Provides
    IRepository repository(Application application) {
        return ((MyApplication) application).getAppComponent().repository();
    }

    @AddEditFragmentScope
    @Provides
    UtilSoftKeyboard utilSoftKeyboard() {
        return new UtilSoftKeyboard();
    }
}

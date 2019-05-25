package com.example.david.lists.di.view.addeditfragment;

import android.app.Application;

import com.example.david.lists.application.MyApplication;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.di.view.ViewScope;
import com.example.david.lists.util.UtilSoftKeyboard;

import dagger.Module;
import dagger.Provides;

@Module
public final class AddEditFragmentCommonModule {
    @ViewScope
    @Provides
    IRepository repository(Application application) {
        return ((MyApplication) application).getAppComponent().repository();
    }

    @ViewScope
    @Provides
    UtilSoftKeyboard utilSoftKeyboard() {
        return new UtilSoftKeyboard();
    }
}

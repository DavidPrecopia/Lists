package com.example.david.lists.di.data;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.david.lists.data.model.IModelContract;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {
        ModelModule.class,
        SharedPreferencesModule.class
})
public interface AppComponent {
    IModelContract getModel();

    SharedPreferences getSharedPrefsNightMode();

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(Application application);
    }
}

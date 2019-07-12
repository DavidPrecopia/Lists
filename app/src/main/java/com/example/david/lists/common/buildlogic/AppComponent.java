package com.example.david.lists.common.buildlogic;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.david.lists.data.remote.buildlogic.RemoteRepositoryModule;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.data.repository.buildlogic.RepositoryModule;
import com.example.david.lists.data.repository.buildlogic.UserRepositoryModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {
        RepositoryModule.class,
        UserRepositoryModule.class,
        RemoteRepositoryModule.class,
        SharedPreferencesModule.class,
        FirebaseAuthModule.class
})
public interface AppComponent {
    IRepositoryContract.Repository repository();

    IRepositoryContract.UserRepository userRepository();

    SharedPreferences sharedPrefsNightMode();

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(Application application);
    }
}

package com.example.david.lists.common.buildlogic;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.NetworkInfo;

import com.example.david.lists.data.remote.buildlogic.RemoteRepositoryModule;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.data.repository.buildlogic.RepositoryModule;
import com.example.david.lists.data.repository.buildlogic.UserRepositoryModule;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {
        RepositoryModule.class,
        UserRepositoryModule.class,
        RemoteRepositoryModule.class,
        SharedPreferencesModule.class,
        FirebaseAuthModule.class,
        NetworkInfoModule.class
})
public interface AppComponent {
    IRepositoryContract.Repository repository();

    IRepositoryContract.UserRepository userRepository();

    SharedPreferences sharedPrefsNightMode();

    @Nullable
    NetworkInfo networkInfo();

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(Application application);
    }
}

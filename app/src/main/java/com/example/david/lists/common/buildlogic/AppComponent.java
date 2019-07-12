package com.example.david.lists.common.buildlogic;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.david.lists.data.remote.buildlogic.RemoteRepositoryModule;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.data.repository.IUserRepository;
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
    IRepository repository();

    IUserRepository userRepository();

    SharedPreferences sharedPrefsNightMode();

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(Application application);
    }
}

package com.example.david.lists.di.view.mainactivity;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.di.view.ViewCommonModule;
import com.example.david.lists.di.view.ViewScope;
import com.example.david.lists.view.MainActivity;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        MainActivityModule.class,
        ViewCommonModule.class
})
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {
        MainActivityComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder activity(AppCompatActivity activity);
    }
}

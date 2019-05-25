package com.example.david.lists.di.view.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.di.view.ViewScope;
import com.example.david.lists.view.MainActivity;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {MainActivityModule.class})
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {
        MainActivityComponent build();

        @BindsInstance
        Builder activity(AppCompatActivity activity);
    }
}

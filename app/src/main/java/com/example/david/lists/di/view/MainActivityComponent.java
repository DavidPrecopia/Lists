package com.example.david.lists.di.view;

import com.example.david.lists.ui.MainActivity;

import dagger.BindsInstance;
import dagger.Component;

@MainActivityScope
@Component(modules = {MainActivityModule.class})
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {
        MainActivityComponent build();

        @BindsInstance
        Builder mainActivity(MainActivity mainActivity);
    }
}

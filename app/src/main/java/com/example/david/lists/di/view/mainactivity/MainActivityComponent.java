package com.example.david.lists.di.view.mainactivity;

import com.example.david.lists.di.view.ViewScope;
import com.example.david.lists.ui.MainActivity;

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
        Builder mainActivity(MainActivity mainActivity);
    }
}

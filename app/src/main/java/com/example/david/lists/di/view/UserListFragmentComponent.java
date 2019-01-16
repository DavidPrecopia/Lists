package com.example.david.lists.di.view;

import android.app.Application;

import com.example.david.lists.ui.view.UserListsFragment;

import dagger.BindsInstance;
import dagger.Component;

@UserListFragmentScope
@Component(modules = {UserListFragmentModule.class})
public interface UserListFragmentComponent {
    void inject(UserListsFragment userListsFragment);

    @Component.Builder
    interface Builder {
        UserListFragmentComponent build();

        @BindsInstance
        Builder application(Application application);
    }
}

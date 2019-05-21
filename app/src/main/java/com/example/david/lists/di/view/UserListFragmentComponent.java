package com.example.david.lists.di.view;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.ui.TouchHelperCallback;
import com.example.david.lists.ui.userlistlist.UserListsFragment;

import dagger.BindsInstance;
import dagger.Component;

@UserListFragmentScope
@Component(modules = {UserListFragmentModule.class, RecyclerViewModule.class})
public interface UserListFragmentComponent {
    void inject(UserListsFragment userListsFragment);

    @Component.Builder
    interface Builder {
        UserListFragmentComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder movementCallback(TouchHelperCallback.MovementCallback movementCallback);
    }
}

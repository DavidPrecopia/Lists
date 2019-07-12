package com.example.david.lists.view.userlistlist.buldlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.common.TouchHelperCallback;
import com.example.david.lists.view.userlistlist.UserListFragment;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        UserListFragmentModule.class,
        ViewCommonModule.class
})
public interface UserListFragmentComponent {
    void inject(UserListFragment userListFragment);

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

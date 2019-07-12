package com.example.david.lists.view.userlistlist.buldlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.common.TouchHelperCallback;
import com.example.david.lists.view.userlistlist.UserListListView;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        UserListListViewModule.class,
        ViewCommonModule.class
})
public interface UserListListViewComponent {
    void inject(UserListListView userListFragment);

    @Component.Builder
    interface Builder {
        UserListListViewComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder movementCallback(TouchHelperCallback.MovementCallback movementCallback);
    }
}

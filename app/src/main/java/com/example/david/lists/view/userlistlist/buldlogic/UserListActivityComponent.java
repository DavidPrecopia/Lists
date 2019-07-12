package com.example.david.lists.view.userlistlist.buldlogic;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.common.buildlogic.ActivityCommonModule;
import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.userlistlist.UserListActivity;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        ActivityCommonModule.class,
        ViewCommonModule.class
})
public interface UserListActivityComponent {
    void inject(UserListActivity activity);

    @Component.Builder
    interface Builder {
        UserListActivityComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder activity(AppCompatActivity appCompatActivity);
    }
}

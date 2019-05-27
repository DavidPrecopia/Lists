package com.example.david.lists.di.view.userlistfragment;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.di.view.common.ActivityCommonModule;
import com.example.david.lists.di.view.common.ViewCommonModule;
import com.example.david.lists.di.view.common.ViewScope;
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

package com.example.david.lists.widget.di;

import android.app.Application;
import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.david.lists.di.view.common.ViewCommonModule;
import com.example.david.lists.di.view.common.ViewScope;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        MyRemoteViewsServiceModule.class,
        ViewCommonModule.class
})
public interface MyRemoteViewsServiceComponent {
    RemoteViewsService.RemoteViewsFactory remoteViewsFactory();

    @Component.Builder
    interface Builder {
        MyRemoteViewsServiceComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder intent(Intent intent);
    }
}

package com.example.david.lists.widget.view.buildlogic;

import android.app.Application;
import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;

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

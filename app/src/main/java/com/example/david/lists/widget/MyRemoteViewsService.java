package com.example.david.lists.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.david.lists.widget.di.DaggerMyRemoteViewsServiceComponent;

public class MyRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return DaggerMyRemoteViewsServiceComponent.builder()
                .application(getApplication())
                .intent(intent)
                .build()
                .remoteViewsFactory();
    }
}

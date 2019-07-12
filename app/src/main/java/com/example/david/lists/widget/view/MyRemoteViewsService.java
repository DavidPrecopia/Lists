package com.example.david.lists.widget.view;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.david.lists.widget.view.buildlogic.DaggerMyRemoteViewsServiceComponent;

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

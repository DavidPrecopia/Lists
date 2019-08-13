package com.example.david.lists.widget.view

import android.content.Intent
import android.widget.RemoteViewsService

import com.example.david.lists.widget.view.buildlogic.DaggerMyRemoteViewsServiceComponent

class MyRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) =
            DaggerMyRemoteViewsServiceComponent.builder()
                    .application(application)
                    .intent(intent)
                    .build()
                    .remoteViewsFactory()
}

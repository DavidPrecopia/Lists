package com.precopia.david.lists.widget.view.buildlogic

import android.app.Application
import android.content.Intent
import android.widget.RemoteViewsService

import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope

import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    MyRemoteViewsServiceModule::class,
    ViewCommonModule::class
])
interface MyRemoteViewsServiceComponent {
    fun remoteViewsFactory(): RemoteViewsService.RemoteViewsFactory

    @Component.Builder
    interface Builder {
        fun build(): MyRemoteViewsServiceComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun intent(intent: Intent): Builder
    }
}

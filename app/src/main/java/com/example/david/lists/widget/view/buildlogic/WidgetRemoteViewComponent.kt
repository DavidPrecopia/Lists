package com.example.david.lists.widget.view.buildlogic

import android.content.Context
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.widget.buildlogic.SharedPrefsModule
import com.example.david.lists.widget.view.WidgetRemoteView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    WidgetRemoteViewModule::class,
    SharedPrefsModule::class
])
interface WidgetRemoteViewComponent {
    fun inject(widgetRemoteView: WidgetRemoteView)

    @Component.Builder
    interface Builder {
        fun build(): WidgetRemoteViewComponent

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun widgetId(appWidgetId: Int): Builder
    }
}
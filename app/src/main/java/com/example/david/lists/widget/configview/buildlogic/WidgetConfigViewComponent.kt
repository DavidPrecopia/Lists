package com.example.david.lists.widget.configview.buildlogic

import android.app.Application
import android.content.Context
import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.widget.common.buildlogic.WidgetCommonModule
import com.example.david.lists.widget.configview.IWidgetConfigContract
import com.example.david.lists.widget.configview.WidgetConfigView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    WidgetConfigViewModule::class,
    WidgetCommonModule::class,
    ViewCommonModule::class
])
interface WidgetConfigViewComponent {

    fun inject(view: WidgetConfigView)

    @Component.Builder
    interface Builder {
        fun build(): WidgetConfigViewComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun view(view: IWidgetConfigContract.View): Builder
    }
}

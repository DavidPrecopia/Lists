package com.precopia.david.lists.widget.configview.buildlogic

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.widget.common.buildlogic.WidgetCommonModule
import com.precopia.david.lists.widget.configview.WidgetConfigView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    WidgetConfigModule::class,
    WidgetCommonModule::class,
    ViewCommonModule::class
])
interface WidgetConfigComponent {

    fun inject(view: WidgetConfigView)

    @Component.Builder
    interface Builder {
        fun build(): WidgetConfigComponent

        @BindsInstance
        fun view(view: AppCompatActivity): Builder

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder
    }
}

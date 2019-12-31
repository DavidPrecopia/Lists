package com.precopia.david.lists.view.common.buildlogic

import android.app.Application
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.common.MainActivity
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    ViewCommonModule::class
])
interface MainActivityComponent {
    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        fun build(): MainActivityComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}
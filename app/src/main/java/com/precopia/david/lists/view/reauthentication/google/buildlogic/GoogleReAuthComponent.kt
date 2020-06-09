package com.precopia.david.lists.view.reauthentication.google.buildlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.reauthentication.google.GoogleReAuthView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    GoogleReAuthModule::class,
    ViewCommonModule::class
])
interface GoogleReAuthComponent {
    fun inject(googleReAuthView: GoogleReAuthView)

    @Component.Builder
    interface Builder {
        fun build(): GoogleReAuthComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: Fragment): Builder
    }
}
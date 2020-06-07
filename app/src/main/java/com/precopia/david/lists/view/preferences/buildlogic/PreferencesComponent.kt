package com.precopia.david.lists.view.preferences.buildlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.preferences.PreferencesView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    PreferencesModule::class,
    ViewCommonModule::class
])
interface PreferencesComponent {
    fun inject(preferencesView: PreferencesView)

    @Component.Builder
    interface Builder {
        fun build(): PreferencesComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: Fragment): Builder
    }
}
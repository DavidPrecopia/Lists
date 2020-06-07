package com.precopia.david.lists.view.authentication.buildlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.authentication.AuthView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    AuthModule::class,
    ViewCommonModule::class
])
interface AuthComponent {
    fun inject(authView: AuthView)

    @Component.Builder
    interface Builder {
        fun build(): AuthComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: Fragment): Builder
    }
}

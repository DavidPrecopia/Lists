package com.precopia.david.lists.view.authentication.buildlogic

import android.app.Application
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.authentication.AuthView
import com.precopia.david.lists.view.authentication.IAuthContract
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    AuthViewModule::class,
    ViewCommonModule::class
])
interface AuthViewComponent {
    fun inject(authView: AuthView)

    @Component.Builder
    interface Builder {
        fun build(): AuthViewComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: IAuthContract.View): Builder
    }
}

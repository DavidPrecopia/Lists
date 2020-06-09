package com.precopia.david.lists.view.reauthentication.email.buildlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.reauthentication.email.EmailReAuthView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    EmailReAuthModule::class,
    ViewCommonModule::class
])
interface EmailReAuthComponent {
    fun inject(emailReAuthComponent: EmailReAuthView)

    @Component.Builder
    interface Builder {
        fun build(): EmailReAuthComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: Fragment): Builder
    }
}
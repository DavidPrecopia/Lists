package com.precopia.david.lists.view.reauthentication.email.buildlogic

import android.app.Application
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.reauthentication.email.EmailReAuthView
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract
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
        fun view(view: IEmailReAuthContract.View): Builder
    }
}
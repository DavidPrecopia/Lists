package com.example.david.lists.view.authentication.emailreauth.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.authentication.emailreauth.EmailReAuthView
import com.example.david.lists.view.authentication.emailreauth.IEmailReAuthContract
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
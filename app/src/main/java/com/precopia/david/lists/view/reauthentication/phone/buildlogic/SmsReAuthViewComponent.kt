package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import android.app.Application
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.reauthentication.phone.ISmsReAuthContract
import com.precopia.david.lists.view.reauthentication.phone.SmsReAuthView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    SmsReAuthViewModule::class,
    ViewCommonModule::class
])
interface SmsReAuthViewComponent {
    fun inject(smsReAuthView: SmsReAuthView)

    @Component.Builder
    interface Builder {
        fun build(): SmsReAuthViewComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: ISmsReAuthContract.View): Builder
    }
}
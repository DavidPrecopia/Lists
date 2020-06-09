package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.reauthentication.phone.SmsReAuthView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    SmsReAuthModule::class,
    ViewCommonModule::class
])
interface SmsReAuthComponent {
    fun inject(smsReAuthView: SmsReAuthView)

    @Component.Builder
    interface Builder {
        fun build(): SmsReAuthComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: Fragment): Builder
    }
}
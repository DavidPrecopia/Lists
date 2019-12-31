package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import android.app.Application
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract
import com.precopia.david.lists.view.reauthentication.phone.PhoneReAuthView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    PhoneReAuthViewModule::class,
    ViewCommonModule::class
])
interface PhoneReAuthViewComponent {
    fun inject(phoneReAuthView: PhoneReAuthView)

    @Component.Builder
    interface Builder {
        fun build(): PhoneReAuthViewComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: IPhoneReAuthContract.View): Builder
    }
}
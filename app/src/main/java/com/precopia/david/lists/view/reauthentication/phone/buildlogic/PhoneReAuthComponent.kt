package com.precopia.david.lists.view.reauthentication.phone.buildlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.reauthentication.phone.PhoneReAuthView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    PhoneReAuthModule::class,
    ViewCommonModule::class
])
interface PhoneReAuthComponent {
    fun inject(phoneReAuthView: PhoneReAuthView)

    @Component.Builder
    interface Builder {
        fun build(): PhoneReAuthComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: Fragment): Builder
    }
}
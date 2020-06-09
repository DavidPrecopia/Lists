package com.precopia.david.lists.view.itemlist.buldlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.common.TouchHelperCallback
import com.precopia.david.lists.view.itemlist.ItemView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    ItemModule::class,
    ViewCommonModule::class
])
interface ItemComponent {
    fun inject(view: ItemView)

    @Component.Builder
    interface Builder {
        fun build(): ItemComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: Fragment): Builder

        @BindsInstance
        fun movementCallback(movementCallback: TouchHelperCallback.MovementCallback): Builder

        @BindsInstance
        fun userListId(userListId: String): Builder
    }
}

package com.precopia.david.lists.view.userlistlist.buldlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.common.TouchHelperCallback
import com.precopia.david.lists.view.userlistlist.UserListView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    UserListModule::class,
    ViewCommonModule::class
])
interface UserListComponent {
    fun inject(userListView: UserListView)

    @Component.Builder
    interface Builder {
        fun build(): UserListComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: Fragment): Builder

        @BindsInstance
        fun movementCallback(movementCallback: TouchHelperCallback.MovementCallback): Builder
    }
}

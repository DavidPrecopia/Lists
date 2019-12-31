package com.precopia.david.lists.view.userlistlist.buldlogic

import android.app.Application
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.common.TouchHelperCallback
import com.precopia.david.lists.view.userlistlist.IUserListViewContract
import com.precopia.david.lists.view.userlistlist.UserListListView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    UserListListViewModule::class,
    ViewCommonModule::class
])
interface UserListListViewComponent {
    fun inject(userListListView: UserListListView)

    @Component.Builder
    interface Builder {
        fun build(): UserListListViewComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: IUserListViewContract.View): Builder

        @BindsInstance
        fun movementCallback(movementCallback: TouchHelperCallback.MovementCallback): Builder
    }
}

package com.example.david.lists.view.userlistlist.buldlogic

import android.app.Application
import androidx.fragment.app.Fragment

import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.common.TouchHelperCallback
import com.example.david.lists.view.userlistlist.IUserListViewContract
import com.example.david.lists.view.userlistlist.UserListListView

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
        fun fragment(fragment: Fragment): Builder

        @BindsInstance
        fun view(view: IUserListViewContract.View): Builder

        @BindsInstance
        fun movementCallback(movementCallback: TouchHelperCallback.MovementCallback): Builder
    }
}

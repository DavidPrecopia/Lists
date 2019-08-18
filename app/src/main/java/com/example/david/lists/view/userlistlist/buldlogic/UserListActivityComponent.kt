package com.example.david.lists.view.userlistlist.buldlogic

import android.app.Application

import androidx.appcompat.app.AppCompatActivity

import com.example.david.lists.common.buildlogic.ActivityCommonModule
import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.userlistlist.UserListActivity

import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    UserListActivityModule::class,
    ActivityCommonModule::class,
    ViewCommonModule::class
])
interface UserListActivityComponent {
    fun inject(activity: UserListActivity)

    @Component.Builder
    interface Builder {
        fun build(): UserListActivityComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun activity(activity: AppCompatActivity): Builder
    }
}

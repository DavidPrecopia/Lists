package com.example.david.lists.view.itemlist.buldlogic

import android.app.Application

import androidx.appcompat.app.AppCompatActivity

import com.example.david.lists.common.buildlogic.ActivityCommonModule
import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.itemlist.ItemActivity

import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    ActivityCommonModule::class,
    ViewCommonModule::class
])
interface ItemActivityComponent {
    fun inject(activity: ItemActivity)

    @Component.Builder
    interface Builder {
        fun build(): ItemActivityComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun activity(appCompatActivity: AppCompatActivity): Builder
    }
}

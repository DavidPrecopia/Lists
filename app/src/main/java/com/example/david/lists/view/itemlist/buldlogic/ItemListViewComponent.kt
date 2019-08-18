package com.example.david.lists.view.itemlist.buldlogic

import android.app.Application

import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.common.TouchHelperCallback
import com.example.david.lists.view.itemlist.IItemViewContract
import com.example.david.lists.view.itemlist.ItemListView

import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    ItemListViewModule::class,
    ViewCommonModule::class
])
interface ItemListViewComponent {
    fun inject(view: ItemListView)

    @Component.Builder
    interface Builder {
        fun build(): ItemListViewComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: IItemViewContract.View): Builder

        @BindsInstance
        fun movementCallback(movementCallback: TouchHelperCallback.MovementCallback): Builder

        @BindsInstance
        fun userListId(userListId: String): Builder
    }
}

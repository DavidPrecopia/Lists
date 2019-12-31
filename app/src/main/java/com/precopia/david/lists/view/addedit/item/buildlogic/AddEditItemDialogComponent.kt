package com.precopia.david.lists.view.addedit.item.buildlogic

import android.app.Application
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.addedit.common.IAddEditContract
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.ID
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.POSITION
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.TITLE
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.USER_LIST_ID
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditDialogCommonModule
import com.precopia.david.lists.view.addedit.item.AddEditItemDialog
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@ViewScope
@Component(modules = [
    AddEditItemDialogModule::class,
    AddEditDialogCommonModule::class,
    ViewCommonModule::class
])
interface AddEditItemDialogComponent {
    fun inject(fragment: AddEditItemDialog)

    @Component.Builder
    interface Builder {
        fun build(): AddEditItemDialogComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: IAddEditContract.View): Builder

        @BindsInstance
        fun id(@Named(ID) id: String): Builder

        @BindsInstance
        fun title(@Named(TITLE) title: String): Builder

        @BindsInstance
        fun userListId(@Named(USER_LIST_ID) userListId: String): Builder

        @BindsInstance
        fun position(@Named(POSITION) position: Int): Builder
    }
}
package com.example.david.lists.view.addedit.userlist.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.addedit.common.IAddEditContract
import com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.ID
import com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.POSITION
import com.example.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.TITLE
import com.example.david.lists.view.addedit.common.buildlogic.AddEditDialogCommonModule
import com.example.david.lists.view.addedit.userlist.AddEditUserListDialog
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@ViewScope
@Component(modules = [
    AddEditUserListDialogModule::class,
    AddEditDialogCommonModule::class,
    ViewCommonModule::class
])
interface AddEditUserListDialogComponent {
    fun inject(fragment: AddEditUserListDialog)

    @Component.Builder
    interface Builder {
        fun build(): AddEditUserListDialogComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: IAddEditContract.View): Builder

        @BindsInstance
        fun id(@Named(ID) id: String): Builder

        @BindsInstance
        fun title(@Named(TITLE) title: String): Builder

        @BindsInstance
        fun position(@Named(POSITION) position: Int): Builder
    }
}

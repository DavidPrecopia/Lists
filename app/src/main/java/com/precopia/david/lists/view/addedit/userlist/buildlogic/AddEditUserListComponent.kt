package com.precopia.david.lists.view.addedit.userlist.buildlogic

import android.app.Application
import androidx.fragment.app.DialogFragment
import com.precopia.david.lists.common.buildlogic.ViewCommonModule
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.ID
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.POSITION
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditCommonNamedConstants.TITLE
import com.precopia.david.lists.view.addedit.common.buildlogic.AddEditDialogCommonModule
import com.precopia.david.lists.view.addedit.userlist.AddEditUserListDialog
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@ViewScope
@Component(modules = [
    AddEditUserListModule::class,
    AddEditDialogCommonModule::class,
    ViewCommonModule::class
])
interface AddEditUserListComponent {
    fun inject(fragment: AddEditUserListDialog)

    @Component.Builder
    interface Builder {
        fun build(): AddEditUserListComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: DialogFragment): Builder

        @BindsInstance
        fun id(@Named(ID) id: String): Builder

        @BindsInstance
        fun title(@Named(TITLE) title: String): Builder

        @BindsInstance
        fun position(@Named(POSITION) position: Int): Builder
    }
}

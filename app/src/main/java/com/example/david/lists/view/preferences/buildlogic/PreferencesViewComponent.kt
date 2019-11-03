package com.example.david.lists.view.preferences.buildlogic

import androidx.fragment.app.Fragment
import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.preferences.IPreferencesViewContract
import com.example.david.lists.view.preferences.PreferencesView
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    PreferencesViewModule::class,
    ViewCommonModule::class
])
interface PreferencesViewComponent {
    fun inject(preferencesView: PreferencesView)

    @Component.Builder
    interface Builder {
        fun build(): PreferencesViewComponent

        @BindsInstance
        fun fragment(fragment: Fragment): Builder

        @BindsInstance
        fun view(view: IPreferencesViewContract.View): Builder
    }
}
package com.example.david.lists.view.authentication.buildlogic

import android.app.Application
import androidx.fragment.app.Fragment
import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.authentication.AuthView
import com.example.david.lists.view.authentication.IAuthContract
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    AuthViewModule::class,
    ViewCommonModule::class
])
interface AuthViewComponent {
    fun inject(authView: AuthView)

    @Component.Builder
    interface Builder {
        fun build(): AuthViewComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: IAuthContract.View): Builder

        @BindsInstance
        fun fragment(fragment: Fragment): Builder
    }
}

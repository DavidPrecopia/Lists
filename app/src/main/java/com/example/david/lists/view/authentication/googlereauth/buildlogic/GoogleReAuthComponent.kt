package com.example.david.lists.view.authentication.googlereauth.buildlogic

import android.app.Application
import com.example.david.lists.common.buildlogic.ViewCommonModule
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.authentication.googlereauth.GoogleReAuthView
import com.example.david.lists.view.authentication.googlereauth.IGoogleReAuthContract
import dagger.BindsInstance
import dagger.Component

@ViewScope
@Component(modules = [
    GoogleReAuthModule::class,
    ViewCommonModule::class
])
interface GoogleReAuthComponent {
    fun inject(googleReAuthView: GoogleReAuthView)

    @Component.Builder
    interface Builder {
        fun build(): GoogleReAuthComponent

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun view(view: IGoogleReAuthContract.View): Builder
    }
}
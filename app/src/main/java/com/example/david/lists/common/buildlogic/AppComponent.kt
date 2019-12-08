package com.example.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import com.example.androiddata.repository.buildlogic.RepositoryComponent
import com.example.david.lists.util.IUtilNightModeContract
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    SharedPrefsModule::class,
    UtilNightModeModule::class
])
interface AppComponent {
    fun utilNightMode(): IUtilNightModeContract

    fun sharedPrefs(): SharedPreferences

    fun repositories(): RepositoryComponent

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

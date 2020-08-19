package com.precopia.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import com.precopia.david.lists.util.IUtilThemeContract
import com.precopia.domain.repository.IRepositoryContract
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RepositoryModule::class,
    SharedPrefsModule::class,
    UtilThemeModule::class
])
interface AppComponent {
    fun utilTheme(): IUtilThemeContract

    fun sharedPrefs(): SharedPreferences

    fun repo(): IRepositoryContract.Repository

    fun userRepo(): IRepositoryContract.UserRepository

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

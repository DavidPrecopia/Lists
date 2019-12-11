package com.example.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import com.example.david.lists.util.IUtilNightModeContract
import com.example.domain.repository.IRepositoryContract
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RepositoryModule::class,
    SharedPrefsModule::class,
    UtilNightModeModule::class
])
interface AppComponent {
    fun utilNightMode(): IUtilNightModeContract

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

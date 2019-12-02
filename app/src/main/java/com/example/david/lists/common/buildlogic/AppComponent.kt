package com.example.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import com.example.androiddata.repository.buildlogic.RepositoryModule
import com.example.androiddata.repository.buildlogic.UserRepositoryModule
import com.example.david.lists.util.IUtilNightModeContract
import com.example.domain.repository.IRepositoryContract
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RepositoryModule::class,
    UserRepositoryModule::class,
    SharedPrefsModule::class,
    UtilNightModeModule::class,
    FirebaseAuthModule::class
])
interface AppComponent {
    fun repo(): IRepositoryContract.Repository

    fun userRepo(): IRepositoryContract.UserRepository

    fun utilNightMode(): IUtilNightModeContract

    fun sharedPrefs(): SharedPreferences

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

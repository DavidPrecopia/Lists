package com.example.david.lists.common.buildlogic

import android.app.Application
import android.content.SharedPreferences
import android.net.NetworkInfo
import com.example.david.lists.data.remote.buildlogic.RemoteRepositoryModule
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.data.repository.buildlogic.RepositoryModule
import com.example.david.lists.data.repository.buildlogic.UserRepositoryModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RepositoryModule::class,
    UserRepositoryModule::class,
    RemoteRepositoryModule::class,
    SharedPreferencesModule::class,
    FirebaseAuthModule::class,
    NetworkInfoModule::class
])
interface AppComponent {
    fun repo(): IRepositoryContract.Repository

    fun userRepo(): IRepositoryContract.UserRepository

    fun sharedPrefsNightMode(): SharedPreferences

    fun networkInfo(): NetworkInfo?

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}

package com.example.david.lists.common.buildlogic

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides

@Module
class NetworkInfoModule {
    @Provides
    fun networkInfo(application: Application): NetworkInfo? {
        return application.getSystemService<ConnectivityManager>()
                ?.activeNetworkInfo
    }
}
